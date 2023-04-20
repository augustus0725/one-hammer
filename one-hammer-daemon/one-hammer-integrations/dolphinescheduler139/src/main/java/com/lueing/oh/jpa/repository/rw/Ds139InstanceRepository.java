package com.lueing.oh.jpa.repository.rw;

import com.lueing.oh.jpa.entity.Ds139Instance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Ds139InstanceRepository extends JpaRepository<Ds139Instance, String> {
    Optional<Ds139Instance> findByInstanceId(Long instanceId);
}
