package models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import dao.*;
import models.Cliente;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author nanda
 */
public class Estacionamento {

    private static int contadorId = 1;
    private int idEstacionamento;
    private int numVagas;
    private String nomeEstacionamento;
    private List<Vaga> vagas;
    private List<Ticket> tickets;
    private List<Cliente> clientes;

    public Estacionamento(String nomeEstacionamento) {
        this.nomeEstacionamento = nomeEstacionamento;
        this.tickets = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.idEstacionamento = contadorId++;
    }

    public void gerarVagas(int numVagas) {
        if (vagas != null) {
            throw new RuntimeException("As vagas ja foram criadas!");
        }
        if (numVagas < 0) {
            throw new IllegalArgumentException("O numero de vagas deve ser valido!");
        } else {
            this.vagas = new ArrayList<>();
            this.numVagas = numVagas;

            for (int i = 0; i < numVagas; i++) {
                Vaga v = new Vaga(idEstacionamento);
                vagas.add(v);
            }
            //EstacionamentoDAO.salvar(this);
            //VagaDAO.salvar(vagas);

        }

    }

    public void imprimirVagas() {
        String status;
        if (!vagas.isEmpty()) {
            for (Vaga v : vagas) {
                if (!v.isOcupada()) {
                    status = "desocupada";
                } else {
                    status = "ocupada";
                }
                System.out.println("ID: " + v.getIdVaga() + ", status: " + status);
            }
        } else {
            throw new IllegalStateException("O estacionamento nao possui vagas criadas.");
        }
    }

    private Vaga localizarVagaLivre() {
        if (this.vagas == null) {
            throw new IllegalStateException("O estacionamento nao possui vagas criadas.");
        }
        for (Vaga vaga : vagas) {
            if (vaga.getStatusVaga().equals("livre")) {
                return vaga; 
            }
        }
        return null; 
    }

    public Ticket gerarTicket(Cliente cliente) {
        //Se o estacionamento nao possui vagas criadas:
        if (vagas.isEmpty()) {
            throw new RuntimeException("Estacionamento nao possui vagas");
        }

        List<Veiculo> veiculosCliente = cliente.getVeiculos();

        //Se o cliente nao possuir veiculo cadastrado:
        if (veiculosCliente.isEmpty()) {
            throw new IllegalStateException("Cliente nao possui veiculo cadastrado.");
        }
        //Se o cliente nao possui um ticket vinculado:
        if (cliente.getTicket() != null) {
            throw new IllegalStateException("Cliente ja possui um ticket vinculado.");
        }

        Vaga vagaLivre = localizarVagaLivre();
        //Se nao ha vaga disponivel: 
        if (vagaLivre == null) {
            throw new RuntimeException("Nao ha vagas disponiveis!");
        }

        Ticket ticket = new Ticket(this.idEstacionamento, vagaLivre);
        cliente.addTicket(ticket);
        //TicketDAO.salvar(ticket); 

        if (!tickets.contains(ticket)) { //Se não há um ticket gerado no estacionamento, 
            tickets.add(ticket);
            atribuirVagaOcupada(vagaLivre);
            return ticket;
        }

        return null;
    }

    public void atribuirVagaOcupada(Vaga vaga) {
        vaga.setStatus(true);
    }

    public void atribuirVagaDesocupada(Vaga vaga) {
        vaga.setStatus(false);
    }

    public void pagarTicket(Cliente cliente, double valor) {
        Ticket ticketCliente = cliente.getTicket();
        LocalDateTime horarioDeSaida = LocalDateTime.now();

        if (ticketCliente == null) {
            throw new RuntimeException("Cliente nao possui ticket vinculado");
        }

        ticketCliente.setHorarioDeSaida(horarioDeSaida);
        double precoTotal = ticketCliente.calcularValorHora();

        if (valor < precoTotal) {
            throw new ArithmeticException("Valor insuficiente");
        }

        Vaga vaga = ticketCliente.getVaga();
        atribuirVagaDesocupada(vaga);
        this.tickets.remove(ticketCliente);
        cliente.removerTicket();

    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public List<Vaga> getVagas() {
        return vagas;
    }

    public int getIdEstacionamento() {
        return idEstacionamento;
    }

    public int getNumVagas() {
        return numVagas;
    }

    public String getNomeEstacionamento() {
        return nomeEstacionamento;
    }

}
//e.pagarTicket(ticket1, arthur); 
