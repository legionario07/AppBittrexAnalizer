package br.com.bittrexanalizer.database.dao;

import java.util.Set;

/**
 * Created by PauLinHo on 24/06/2017.
 */

/**
 * Interface IDAO implementada por todas classes que persistem objetos no BD
 */
public interface IDAO<T extends Object> {

    /**
     *
     * @param - Recebe uma entidade Dominio para persistir no BD
     * @return Um Long com o id da entidade armazenada no BD
     */
    Long create(T p);

    /**
     * @return retorno o numero de linhas afetadas ou 0 se houve erro
     * @param p recebe uma entidade Dominio e altera a entidade com o mesmo id no BD
     */
    long update(T p);

    /**
     *
     * @param p recebe uma entidade Dominio e remove a entidade com o mesmo id no BD
     */
    void delete(T p);

    /**
     *
     * @param p recebe uma entidade dominio e pesquisa no BD a entidade com o mesmo id
     * @return uma entidade do bd ou null se nao encontrar
     */
    T find(T p);

    /**
     *
     * @return todas as entidades no BD
     */
    Set<T> findAll();
}
