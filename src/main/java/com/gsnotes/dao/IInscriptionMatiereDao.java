package com.gsnotes.dao;

import com.gsnotes.bo.InscriptionMatiere;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInscriptionMatiereDao extends JpaRepository<InscriptionMatiere, Long> {
}
