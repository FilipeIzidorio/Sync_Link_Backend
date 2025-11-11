package com.synclink.application.service;

import com.synclink.application.dto.MesaDTO;
import com.synclink.model.StatusMesa;
import java.util.List;

public interface MesaService {

    List<MesaDTO> findAll();
    MesaDTO findById(Long id);
    MesaDTO create(MesaDTO mesaDTO);
    MesaDTO update(Long id, MesaDTO mesaDTO);
    void delete(Long id);
    MesaDTO updateStatus(Long id, StatusMesa status);
    List<MesaDTO> findByStatus(StatusMesa status);
    List<MesaDTO> findMesasLivres();
}