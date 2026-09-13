package com.pillone.pillone.service;

import com.pillone.pillone.model.FormulasMedicas;
import com.pillone.pillone.repository.DetallesFormulasRepository;
import com.pillone.pillone.repository.FormulasMedicasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FormulasService {

    @Autowired
    private FormulasMedicasRepository formulasMedicasRepository;

    @Autowired
    private DetallesFormulasRepository detallesFormulasRepository;

    public boolean tieneFormulaVigente(
            Long idCliente,
            Long idProducto
    ) {

        return obtenerFormulaVigente(
                idCliente,
                idProducto
        ) != null;
    }

    public FormulasMedicas obtenerFormulaVigente(
            Long idCliente,
            Long idProducto
    ) {

        if (
                idCliente == null
                        ||
                        idProducto == null
        ) {
            return null;
        }

        List<FormulasMedicas> formulas =
                formulasMedicasRepository
                        .findByIdCliente(
                                idCliente
                        );

        LocalDate hoy =
                LocalDate.now();

        for (
                FormulasMedicas formula
                :
                formulas
        ) {

            if (
                    formula.getFechaExpedicion()
                            ==
                            null
            ) {
                continue;
            }

            boolean vigente =
                    formula.isVigente(
                            hoy
                    );

            if (
                    vigente
                            &&
                            detallesFormulasRepository
                                    .existsByIdFormulaAndIdProducto(
                                            formula.getIdFormula(),
                                            idProducto
                                    )
            ) {

                return formula;
            }
        }

        return null;
    }
}