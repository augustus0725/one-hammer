package com.lueing.oh.jpa.repository.rw;

import com.lueing.oh.jpa.entity.OneHammerJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OneHammerJobRepository extends JpaRepository<OneHammerJob, String> {
    Optional<OneHammerJob> findByNamespaceAndName(String namespace, String name);
}
