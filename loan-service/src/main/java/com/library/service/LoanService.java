package com.library.service;

import com.library.client.BookClient;
import com.library.client.UserClient;
import com.library.dto.BookDto;
import com.library.dto.LoanDto;
import com.library.dto.LoanRequest;
import com.library.dto.UserDto;
import com.library.exception.LoanNotFoundException;
import com.library.exception.LoanAlreadyActiveException;
import com.library.model.Loan;
import com.library.model.Loan.LoanStatus;
import com.library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private static final int LOAN_DURATION_DAYS = 14;

    private final LoanRepository loanRepository;
    private final BookClient bookClient;
    private final UserClient userClient;

    public List<LoanDto> getAllLoans() {
        return loanRepository.findAll().stream()
                .map(this::enrichLoan)
                .collect(Collectors.toList());
    }

    public LoanDto getLoanById(Long id) {
        Loan loan = findLoanById(id);
        return enrichLoan(loan);
    }

    public List<LoanDto> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId).stream()
                .map(this::enrichLoan)
                .collect(Collectors.toList());
    }

    public List<LoanDto> getActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE).stream()
                .map(this::enrichLoan)
                .collect(Collectors.toList());
    }

    public List<LoanDto> getOverdueLoans() {
        LocalDate today = LocalDate.now();
        // Update overdue status first
        List<Loan> overdueLoans = loanRepository.findByDueDateBeforeAndStatus(today, LoanStatus.ACTIVE);
        overdueLoans.forEach(loan -> {
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);
        });
        return loanRepository.findByStatus(LoanStatus.OVERDUE).stream()
                .map(this::enrichLoan)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanDto createLoan(LoanRequest request) {
        // Validate user exists and is active
        UserDto user = userClient.getUserById(request.getUserId());
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new IllegalStateException("User is not active: " + user.getName());
        }

        // Check for existing active loan of same book by same user
        if (loanRepository.existsByUserIdAndBookIdAndStatus(
                request.getUserId(), request.getBookId(), LoanStatus.ACTIVE)) {
            throw new LoanAlreadyActiveException("User already has an active loan for this book");
        }

        // Validate book and decrease available copies (throws if no copies)
        BookDto book = bookClient.decreaseAvailableCopies(request.getBookId());

        log.info("Creating loan: user={}, book={}", user.getName(), book.getTitle());

        Loan loan = Loan.builder()
                .userId(request.getUserId())
                .bookId(request.getBookId())
                .loanDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(LOAN_DURATION_DAYS))
                .status(LoanStatus.ACTIVE)
                .build();

        Loan saved = loanRepository.save(loan);
        return enrichLoan(saved);
    }

    @Transactional
    public LoanDto returnBook(Long loanId) {
        Loan loan = findLoanById(loanId);

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException("Book already returned for loan: " + loanId);
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        // Increase available copies back
        bookClient.increaseAvailableCopies(loan.getBookId());

        log.info("Book returned for loan: {}", loanId);
        return enrichLoan(loanRepository.save(loan));
    }

    private Loan findLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with id: " + id));
    }

    private LoanDto enrichLoan(Loan loan) {
        LoanDto dto = LoanDto.builder()
                .id(loan.getId())
                .userId(loan.getUserId())
                .bookId(loan.getBookId())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnDate(loan.getReturnDate())
                .status(loan.getStatus())
                .build();
        try {
            UserDto user = userClient.getUserById(loan.getUserId());
            dto.setUserName(user.getName());
        } catch (Exception e) {
            log.warn("Could not fetch user details for loan {}", loan.getId());
        }
        try {
            BookDto book = bookClient.getBookById(loan.getBookId());
            dto.setBookTitle(book.getTitle());
        } catch (Exception e) {
            log.warn("Could not fetch book details for loan {}", loan.getId());
        }
        return dto;
    }
}
