package com.synclink.application.mapper;

import com.synclink.application.dto.PagamentoDTO;
import com.synclink.model.Pagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {
    PagamentoMapper INSTANCE = Mappers.getMapper(PagamentoMapper.class);


    PagamentoDTO toDto(Pagamento pagamento);


    Pagamento toEntity(PagamentoDTO pagamentoDTO);

    List<PagamentoDTO> toDtoList(List<Pagamento> pagamentos);


    void updateEntityFromDto(PagamentoDTO pagamentoDTO, @MappingTarget Pagamento pagamento);
}