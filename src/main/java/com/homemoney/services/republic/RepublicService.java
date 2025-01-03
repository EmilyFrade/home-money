package com.homemoney.services.republic;

import com.homemoney.model.republic.Republic;
import com.homemoney.repositories.republic.RepublicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepublicService {

    @Autowired
    private RepublicRepository republicRepository;

    // Salva ou atualiza a república
    public void save(Republic republic) {
        republicRepository.save(republic);
    }

    // Busca uma república por ID
    public Republic findById(long id) {
        Optional<Republic> republic = republicRepository.findById(id);
        return republic.orElse(null); // Retorna null se a república não for encontrada
    }

    // Busca todas as repúblicas
    public List<Republic> findAll() {
        return republicRepository.findAll();
    }
}
