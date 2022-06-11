package com.gsnotes.dao;

import com.gsnotes.bo.InscriptionModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInscriptionModuleDao extends JpaRepository<InscriptionModule, Long> {
}
