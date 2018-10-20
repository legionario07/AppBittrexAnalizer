package br.com.bittrexanalizer.domain;

/**
 * Created by PauLinHo on 12/01/2018.
 */

/**
 * Esta classe representa as configurações do sistema,
 * onde pode ser alterado os valores manualmente, assim o sistema entendera
 */
public class Configuracao extends EntidadeDomain{

    private Long id;
    private String propriedade;
    private String valor;

    public Configuracao(Long id, String propriedade, String valor) {
        this(propriedade, valor);
        this.id = id;
    }

    public Configuracao(String propriedade, String valor) {
        this.propriedade = propriedade;
        this.valor = valor;
    }



    public Configuracao(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(String propriedade) {
        this.propriedade = propriedade;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Configuracao{" +
                "id=" + id +
                ", propriedade='" + propriedade + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Configuracao)) return false;

        Configuracao that = (Configuracao) o;

        if (propriedade != null ? !propriedade.equals(that.propriedade) : that.propriedade != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return propriedade != null ? propriedade.hashCode() : 0;
    }
}
