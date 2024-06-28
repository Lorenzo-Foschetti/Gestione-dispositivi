package lorenzofoschetti.u5d10.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lorenzofoschetti.u5d10.entities.Dipendente;
import lorenzofoschetti.u5d10.exceptions.BadRequestException;
import lorenzofoschetti.u5d10.exceptions.NotFoundException;
import lorenzofoschetti.u5d10.payloads.NewDipendentePayload;
import lorenzofoschetti.u5d10.repositories.DipendenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class DipendenteService {

    @Autowired
    private Cloudinary cloudinaryUploader;

    @Autowired
    private DipendenteRepository dipendenteRepository;

    public Page<Dipendente> getDipendentiList(int pageNumber, int pageSize, String sortBy) {
        if (pageSize > 20) pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        return dipendenteRepository.findAll(pageable);
    }

    public Dipendente save(NewDipendentePayload body) {

        this.dipendenteRepository.findByEmail(body.email()).ifPresent(

                user -> {
                    throw new BadRequestException("L'email " + body.email() + " è già in uso!");
                }
        );


        Dipendente newDipendente = new Dipendente(body.name(), body.surname(), body.username(), body.email());

        newDipendente.setAvatar("https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());


        return dipendenteRepository.save(newDipendente);
    }


    public Dipendente findById(UUID id) {
        return dipendenteRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public Dipendente findByIdAndUpdate(UUID id, Dipendente dipendenteModificato) {
        Dipendente found = findById(id);
        found.setEmail(dipendenteModificato.getEmail());
        found.setName(dipendenteModificato.getName());
        found.setEmail(dipendenteModificato.getEmail());
        found.setSurname(dipendenteModificato.getSurname());
        found.setAvatar(dipendenteModificato.getAvatar());


        return dipendenteRepository.save(found);
    }

    public void findByIdAndDelete(UUID dipendenteId) {
        Dipendente found = this.findById(dipendenteId);
        dipendenteRepository.delete(found);
    }

    public String uploadImage(MultipartFile file) throws IOException {
        return (String) cloudinaryUploader.uploader().upload(file.getBytes(), ObjectUtils.emptyMap()).get("url");
    }

    public Dipendente saveImageUrl(String url, UUID dipendenteId) {
        Dipendente found = findById(dipendenteId);
        found.setAvatar(url);
        return dipendenteRepository.save(found);
    }
}
