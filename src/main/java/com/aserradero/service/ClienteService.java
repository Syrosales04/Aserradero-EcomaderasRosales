package com.aserradero.service;

import com.aserradero.exception.RecursoNoEncontradoException;
import com.aserradero.model.Cliente;
import com.aserradero.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id " + id));
    }

    public Cliente crear(Cliente cliente) {
        cliente.setId(null);
        if (cliente.getEstado() == null) {
            cliente.setEstado(true);
        }
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente datos) {
        Cliente c = buscarPorId(id);
        c.setNombre(datos.getNombre());
        c.setCedula(datos.getCedula());
        c.setTelefono(datos.getTelefono());
        c.setCorreo(datos.getCorreo());
        c.setDireccion(datos.getDireccion());
        if (datos.getEstado() != null) {
            c.setEstado(datos.getEstado());
        }
        return clienteRepository.save(c);
    }

    public Cliente desactivar(Long id) {
        Cliente c = buscarPorId(id);
        c.setEstado(false);
        return clienteRepository.save(c);
    }

    public Cliente activar(Long id) {
    Cliente c = buscarPorId(id);
    c.setEstado(true);
    return clienteRepository.save(c);
}

}
