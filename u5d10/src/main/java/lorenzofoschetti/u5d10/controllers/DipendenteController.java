package lorenzofoschetti.u5d10.controllers;


import lorenzofoschetti.u5d10.entities.Dipendente;
import lorenzofoschetti.u5d10.exceptions.BadRequestException;
import lorenzofoschetti.u5d10.payloads.NewDipendentePayload;
import lorenzofoschetti.u5d10.services.DipendenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/dipendenti")
public class DipendenteController {

    @Autowired
    private DipendenteService dipendenteService;


    @GetMapping
    private Page<Dipendente> getAllDipendenti(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sortBy) {
        return this.dipendenteService.getDipendentiList(page, size, sortBy);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)

    private Dipendente saveDipendente(@RequestBody @Validated NewDipendentePayload body, BindingResult validationResult) {

        if (validationResult.hasErrors()) {

            throw new BadRequestException(validationResult.getAllErrors());
        }
        return dipendenteService.save(body);
    }

    @GetMapping("/{dipendenteId}")
    public Dipendente findById(@PathVariable UUID dipendenteId) {
        return dipendenteService.findById(dipendenteId);
    }

    @PutMapping("/{dipendenteId}")
    public Dipendente findByIdAndUpdate(@PathVariable UUID dipendenteId, @RequestBody Dipendente dipendeModificato) {
        return dipendenteService.findByIdAndUpdate(dipendenteId, dipendeModificato);
    }

    @DeleteMapping("/{dipendenteId}")
    public void findByIdAndDelete(@PathVariable UUID dipendenteId) {
        dipendenteService.findByIdAndDelete(dipendenteId);

    }

    @PostMapping("/{dipendenteId}/avatar")
    public Dipendente uploadAvatarImage(@PathVariable UUID dipendenteId, @RequestParam("avatar") MultipartFile image) throws IOException {
        String imageUrl = dipendenteService.uploadImage(image);
        Dipendente dipendenteModificato = dipendenteService.saveImageUrl(imageUrl, dipendenteId);

        return dipendenteModificato;
    }
}
