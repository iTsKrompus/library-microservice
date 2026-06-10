package com.library.repository;

import com.library.model.Loan;
import com.library.model.Loan.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);
    List<Loan> findByDueDateBeforeAndStatus(LocalDate date, LoanStatus status);
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, LoanStatus status);
}
