package br.com.bittrexanalizer.domain;

import android.support.annotation.NonNull;

import static android.R.attr.value;

/**
 * Created by PauLinHo on 11/01/2018.
 */

public class Trader implements Comparable<Trader>{

    private Long id;
    private String nome;
    private String email;
    private String telefone;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trader)) return false;

        Trader trader = (Trader) o;

        if (id != null ? !id.equals(trader.id) : trader.id != null) return false;
        return email != null ? email.equals(trader.email) : trader.email == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Trader{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Trader trader) {
        int valor = (this.getId() > trader.getId()) ? 1 : 0;
        return valor;
    }
}
