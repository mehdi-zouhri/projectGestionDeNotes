package com.gsnotes.services.impl;

import com.gsnotes.bo.*;
import com.gsnotes.bo.Module;
import com.gsnotes.dao.*;
import com.gsnotes.services.IEnseignantService;
import com.gsnotes.utils.ExcelImporter;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class EnseignantServiceImpl implements IEnseignantService {
	@Autowired
	ServletContext context;

	@Autowired
	IEnseignantDao enseignantDao;

	@Autowired
	IElementDao elementDao;

	@Autowired
	IModuleDao moduleDao;

	@Autowired
	IInscriptionAnnuelleDao inscriptionAnnuelleDao;

	@Autowired
	IInscriptionModuleDao inscriptionModuleDao;

	@Autowired
	IInscriptionMatiereDao inscriptionMatiereDao;

	@Autowired
	IEtudiantDao etudiantDao;

	@Autowired
	IUtilisateurDao userDao;

	@Override
	public void processExcelFile(String filename, boolean overwrite) throws Exception {
		List<ArrayList<Object>> lst = null;

		List<Double> listDoubles = new ArrayList<Double>();
		double noteMax = 20;

		double noteMin = 0;

//		if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType())) {
//
//			// validate file type
//			throw new Exception("Please select an excell file to upload.");
//
//		}
		lst = ExcelImporter.importExcel(filename);

		String moduleName = ((String) lst.get(0).get(1));
		String profName = ((String) lst.get(1).get(1)).replace("\n", " ");
		String session = (String) lst.get(0).get(3);
		String className = (String) lst.get(1).get(5);
		int year = Integer.valueOf(((String) lst.get(0).get(5)).split("/")[1]);

		List<Utilisateur> users = userDao.getUtilisateurByNom(profName);
		if (users == null || users.isEmpty()) {
			throw new Exception("prof n'existe pas dans la base de donnee");
		}


		String fname = context.getRealPath("uploads") + moduleName + Calendar.getInstance().get(Calendar.YEAR);
		File f = new File(fname);
		System.out.println(f.getName());
		if (f.exists() && !overwrite) {
			throw new FileAlreadyExistsException("err");
		}
		Files.copy(new File(filename).toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		if (overwrite)
			System.out.println("overwriting");

		Module module = moduleDao.getByTitre(moduleName);
		int nbElement = moduleDao.getElementCount(module.getIdModule());
		System.out.println("nombre d'elements: " + nbElement);


		for (int k = 4; k < lst.size(); k++) {
			ArrayList<InscriptionMatiere> ies = new ArrayList<InscriptionMatiere>();
			double moyenne = 0;
			for (int i = 4; i < 4 + nbElement; i++) {
				double note = (double) lst.get(k).get(i);
				if (note < 0 || note > 20)
					throw new Exception("la note doit etre entre 0 et 20");

				String element = (String) lst.get(3).get(i);
				Element elem = elementDao.getByTitre(element);
				moyenne += elem.getCurrentCoefficient() * note;
				System.out.println(" + " + elem.getCurrentCoefficient() + " * " + note + " ");

				InscriptionMatiere ie = new InscriptionMatiere();
				ie.setCoefficient(elem.getCurrentCoefficient());
				ie.setNoteFinale(note);
				ie.setNoteSN(note);
				ie.setNoteSR(0);
				ie.setPlusInfos("infos");
				ie.setMatiere(elem);

				ies.add(ie);
			}

			InscriptionModule im = new InscriptionModule();
			im.setModule(module);
			im.setNoteFinale(moyenne);
			im.setNoteSN(moyenne);
			im.setNoteSR(0);
			im.setPlusInfos("infos");
			im.setValidation(moyenne >= 12 ? "V" : "NV");

			InscriptionAnnuelle ia = new InscriptionAnnuelle();
			ia.setAnnee(year);
			ia.setEtat(0);
			ia.setValidation(im.getValidation());
			ia.setPlusInfos("");
			ia.setRang(0);
			//Etudiant etd = etudiantDao.getByCne((String) lst.get(k).get(1));
			//ia.setEtudiant(etd);
			ia.setMention("mention");
			ia.setType("type");

			im.setInscriptionAnnuelle(ia);

				inscriptionAnnuelleDao.save(ia);
			inscriptionModuleDao.save(im);
			for (InscriptionMatiere ie : ies) {
				ie.setInscriptionAnnuelle(ia);
				inscriptionMatiereDao.save(ie);
			}

		}



	}
	
	@Override
	public Enseignant getEnseignantById(Long id)
	
	{	
		return enseignantDao.getEnseignantByIdUtilisateur(id);
	}
	public void save(MultipartFile file, String filename) {
		try {
			Files.copy(file.getInputStream(), Paths.get(filename), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			throw new RuntimeException("Impossible de stocker le fichier. Erreur: " + e.getMessage());
		}
	}

	public void clear() {
		inscriptionAnnuelleDao.deleteAll();
		inscriptionModuleDao.deleteAll();
		inscriptionMatiereDao.deleteAll();
	}
}
