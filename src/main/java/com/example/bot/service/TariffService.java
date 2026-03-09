package com.example.bot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bot.entity.Tariff;
import com.example.bot.repository.TariffRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TariffService {
    @Autowired
    private TariffRepository tariffRepository;

    public List<Tariff> findAll() {
        return tariffRepository.findAll();
    }

    public Tariff save(Tariff tariff) {
        return tariffRepository.save(tariff);
    }

    public void deleteById(Long id) {
        tariffRepository.deleteById(id);
    }

    public void toggleActive(Long id) {
        Tariff tariff = tariffRepository.findById(id)
            .orElseThrow();
        tariff.setActive(!tariff.isActive());
        tariffRepository.save(tariff);
    }
}
/*public class TariffService {
    private final TariffRepository tariffRepository;
    public TariffService(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    public List<Tariff>getListTariff() {
        return tariffRepository.findAll();
    }
}*/
