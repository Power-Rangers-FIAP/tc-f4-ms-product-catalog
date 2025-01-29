package br.com.powerprogramers.product.domain.batch;

import br.com.powerprogramers.product.domain.model.Load;

/** Interface that represents a product batch executor. */
public interface ProductBatchExecutor {
  /**
   * Executes a product batch.
   *
   * @param load load class
   */
  void execute(Load load);
}
