package com.lueing.oh.jpa.repository.rw;

import com.lueing.oh.jpa.entity.Ds139Namespace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Ds139NamespaceRepository extends JpaRepository<Ds139Namespace, String> {
    Optional<Ds139Namespace> findByNamespace(String namespace);
}
