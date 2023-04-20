package com.lueing.oh.jpa.repository.rw;

import com.lueing.oh.jpa.entity.Ds139Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Ds139TemplateRepository extends JpaRepository<Ds139Template, String> {
    Optional<Ds139Template> findByNamespaceAndName(String namespace, String templateName);

    List<Ds139Template> findAllByNamespace(String namespace);
}
