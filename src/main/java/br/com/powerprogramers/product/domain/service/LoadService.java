package br.com.powerprogramers.product.domain.service;

import br.com.powerprogramers.product.domain.dto.LoadJobDto;
import br.com.powerprogramers.product.domain.model.Load;
import java.time.LocalDateTime;

/** Interface that represents the load service. */
public interface LoadService {

  /**
   * Load products from a file.
   *
   * @param load file to be loaded
   */
  void load(Load load);

  /**
   * Schedule a job to load products from a file.
   *
   * @param startTime time to start the job
   * @param load file to be loaded
   * @return the job that was scheduled
   */
  LoadJobDto scheduleJob(LocalDateTime startTime, Load load);

  /**
   * Cancel a job.
   *
   * @param jobId job id
   * @return true if the job was canceled, false otherwise
   */
  boolean cancelJob(String jobId);
}
