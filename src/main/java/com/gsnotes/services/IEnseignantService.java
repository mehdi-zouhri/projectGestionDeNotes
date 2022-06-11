package com.gsnotes.services;

import com.gsnotes.bo.Enseignant;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

public interface IEnseignantService {
	

	public Enseignant getEnseignantById(Long idPersonne);

		public void save(MultipartFile file, String filename);
		public void processExcelFile(String filename, boolean overwrite) throws Exception;
		public void clear();

}
