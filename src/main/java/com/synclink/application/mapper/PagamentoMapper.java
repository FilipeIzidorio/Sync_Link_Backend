package com.synclink.application.mapper;

import com.synclink.application.dto.PagamentoDTO;
import com.synclink.model.Pagamento;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    PagamentoMapper INSTANCE = Mappers.getMapper(PagamentoMapper.class);

    @Mapping(target = "pedidoId", source = "pedido.id")
    @Mapping(target = "pedidoTotal", source = "pedido.valorFinal")
    PagamentoDTO toDto(Pagamento pagamento);

    Pagamento toEntity(PagamentoDTO dto);

    List<PagamentoDTO> toDtoList(List<Pagamento> pagamentos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PagamentoDTO dto, @MappingTarget Pagamento entity);
}
