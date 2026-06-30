package com.aserradero.service;

import com.aserradero.exception.RecursoNoEncontradoException;
import com.aserradero.model.Proveedor;
import com.aserradero.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public List<Proveedor> listar() {
        return proveedorRepository.findAll();
    }

    public Proveedor buscarPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id " + id));
    }

    public Proveedor crear(Proveedor proveedor) {
        proveedor.setId(null);
        if (proveedor.getEstado() == null) {
            proveedor.setEstado(true);
        }
        return proveedorRepository.save(proveedor);
    }

    public Proveedor actualizar(Long id, Proveedor datos) {
        Proveedor p = buscarPorId(id);
        p.setNombre(datos.getNombre());
        p.setCedulaJuridica(datos.getCedulaJuridica());
        p.setTelefono(datos.getTelefono());
        p.setCorreo(datos.getCorreo());
        p.setDireccion(datos.getDireccion());
        if (datos.getEstado() != null) {
            p.setEstado(datos.getEstado());
        }
        return proveedorRepository.save(p);
    }

    /** Borrado logico: se marca como inactivo en vez de eliminar. */
    public Proveedor desactivar(Long id) {
        Proveedor p = buscarPorId(id);
        p.setEstado(false);
        return proveedorRepository.save(p);
    }
}
