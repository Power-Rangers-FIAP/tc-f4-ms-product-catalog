package br.com.powerprogramers.product.domain.service.impl;

import br.com.powerprogramers.product.domain.batch.ProductBatchExecutor;
import br.com.powerprogramers.product.domain.dto.LoadJobDto;
import br.com.powerprogramers.product.domain.exceptions.ProductLoadJobException;
import br.com.powerprogramers.product.domain.model.Load;
import br.com.powerprogramers.product.domain.service.LoadService;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Class that controls the loading of products from a file. */
@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class LoadServiceImpl implements LoadService {

  private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
  private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
  private final ProductBatchExecutor productBatchExecutor;

  @Override
  public void load(Load load) {
    try {
      Files.createDirectories(load.getPath());

      Files.write(load.getFullPath(), load.getBinary());
    } catch (Exception e) {
      throw new ProductLoadJobException(e.getMessage());
    }

    this.productBatchExecutor.execute(load);
  }

  @Override
  public LoadJobDto scheduleJob(LocalDateTime startTime, Load load) {
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.now(zoneId);

    if (localDateTime.isAfter(startTime)) {
      throw new ProductLoadJobException("invalid execution date");
    }
    var delay = Duration.between(localDateTime, startTime).toMillis();
    String jobId = "job-" + Instant.now().toEpochMilli();

    ScheduledFuture<?> scheduledFuture =
        this.ses.schedule(() -> load(load), delay, TimeUnit.MILLISECONDS);

    this.scheduledTasks.put(jobId, scheduledFuture);

    return new LoadJobDto().id(jobId).scheduledDate(startTime.atOffset(ZoneOffset.UTC));
  }

  @Override
  public boolean cancelJob(String jobId) {
    ScheduledFuture<?> scheduledFuture = scheduledTasks.remove(jobId);
    return scheduledFuture != null && scheduledFuture.cancel(false);
  }
}
