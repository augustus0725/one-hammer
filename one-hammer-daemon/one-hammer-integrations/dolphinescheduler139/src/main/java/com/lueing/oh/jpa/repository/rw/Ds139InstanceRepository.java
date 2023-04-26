package com.lueing.oh.jpa.repository.rw;

import com.lueing.oh.jpa.entity.Ds139Dag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Ds139InstanceRepository extends JpaRepository<Ds139Dag, String> {
    Optional<Ds139Dag> findByDagId(Long dagId);

    void deleteByNamespaceAndDagId(String namespace, long dagId);
}
