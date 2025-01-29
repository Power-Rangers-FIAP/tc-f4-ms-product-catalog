package br.com.powerprogramers.product.domain.service.usecase;

/**
 * Use case interface to follow dependency reverse.
 *
 * @param <I> Input object
 * @param <O> Output object
 */
public interface UseCase<I, O> {

  /**
   * Standard method for executing the use cases.
   *
   * @param input input object for the use case
   * @return output object for the use case
   */
  O execute(I input);
}
