package com.synclink.application.service;

import com.synclink.application.dto.MesaDTO;
import com.synclink.application.dto.MesaResumoDTO;
import com.synclink.model.enums.StatusMesa;
import java.util.List;

public interface MesaService {

    List<MesaDTO> findAll();

    MesaDTO findById(Long id);

    MesaDTO create(MesaDTO dto);

    MesaDTO update(Long id, MesaDTO dto);

    void delete(Long id);

    MesaDTO updateStatus(Long id, StatusMesa status);

    List<MesaDTO> findByStatus(StatusMesa status);

    List<MesaDTO> findMesasLivres();

    List<MesaResumoDTO> findResumo();
}
