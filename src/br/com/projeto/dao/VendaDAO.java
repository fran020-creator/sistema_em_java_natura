/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.projeto.dao;

import br.com.projeto.jdbc.ConnectionFactory;
import br.com.projeto.model.Pessoa;
import br.com.projeto.model.ItemVenda;
import br.com.projeto.model.Produto;

import br.com.projeto.model.Venda;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author franc
 */
public class VendaDAO {

    private Connection con;

    public VendaDAO() {
        this.con = new ConnectionFactory().getConnection();
    }

    //Cadastrar Venda
    public void cadastrarVenda(Venda obj) {
        try {

            String sql = "insert into tb_vendas (cliente_id, data_venda,total_venda,observacoes) values (?,?,?,?)";
            //2 passo - conectar o banco de dados e organizar o comando sql
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setInt(1, obj.getCliente().getId());
            stmt.setString(2, obj.getData_venda());
            stmt.setDouble(3, obj.getTotal_venda());
            stmt.setString(4, obj.getObs());

            stmt.execute();
            stmt.close();
           

        } catch (Exception erro) {

            JOptionPane.showMessageDialog(null, "Erro : " + erro);

        }

    }

    //Retorna a ultima venda
    public int retornaUltimaVenda() {
        try {
            int idvenda = 0;

            String sql = "select max(id) id from tb_vendas";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Venda p = new Venda();

                p.setId(rs.getInt("id"));
                idvenda = p.getId();
            }

            return idvenda;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //Metodo que filtra Vendas por Datas
    public List<Venda> listarVendasPorPeriodo(LocalDate data_inicio, LocalDate data_fim) {
        try {

            //1 passo criar a lista
            List<Venda> lista = new ArrayList<>();

            //2 passo - criar o sql , organizar e executar.
            String sql = "select v.id ,  date_format(v.data_venda,'%d/%m/%Y') as data_formatada , c.nome, v.total_venda, v.observacoes  from tb_vendas as v  "
                    + " inner join tb_clientes as c on(v.cliente_id = c.id) where v.data_venda BETWEEN ? AND ?";

            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, data_inicio.toString());
            stmt.setString(2, data_fim.toString());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Venda obj = new Venda();
                Pessoa c = new Pessoa();

                obj.setId(rs.getInt("v.id"));
                obj.setData_venda(rs.getString("data_formatada"));
                c.setNome(rs.getString("c.nome"));
                obj.setTotal_venda(rs.getDouble("v.total_venda"));
                obj.setObs(rs.getString("v.observacoes"));

                obj.setCliente(c);

                lista.add(obj);
            }

            return lista;

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(null, "Erro :" + erro);
            return null;
        }

    }

    //Metodo que calcula total da venda por data
    public double retornaTotalVendaPorData(LocalDate data_venda) {
        try {

            double totalvenda = 0;

            String sql = "select sum(total_venda) as total from tb_vendas where data_venda = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, data_venda.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                totalvenda = rs.getDouble("total");
            }

            return totalvenda;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    //metodo que lista itens de uma venda por id
    
    public List<ItemVenda> listaItensPorVenda(int venda_id) {
        
        List<ItemVenda> lista = new ArrayList<>();
//        select p.descricao, i.qtd, p.preco, i.subtotal from tb_itensvendas as i
//inner join tb_produtos as p on(i.produto_id = p.id) where i.venda_id =10; 
        try {
            String query = "select p.descricao, i.qtd, p.preco, i.subtotal from tb_itensvendas as i "
                                 + " inner join tb_produtos as p on(i.produto_id = p.id) where i.venda_id = ? ";
       
            PreparedStatement ps = con.prepareStatement(query);         
            ps.setInt(1, venda_id);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ItemVenda item = new ItemVenda();
                Produto prod = new Produto();
                
                 prod.setDescricao(rs.getString("p.descricao"));
                 item.setQtd(rs.getInt("i.qtd"));
                 prod.setPreco(rs.getDouble("p.preco"));
                 item.setSubtotal(rs.getDouble("i.subtotal"));
                
                 item.setProduto(prod);         
              
                
                lista.add(item);
            }
            return lista;
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }

    
}
