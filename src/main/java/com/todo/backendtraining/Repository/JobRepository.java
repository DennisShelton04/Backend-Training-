package com.todo.backendtraining.Repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.backendtraining.Entity.Job;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JobRepository {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Path filePath;
  private Long nextId = 1L;

  public JobRepository() {
    try {
      filePath = Paths.get(new ClassPathResource("JOBS.txt").getURI());
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize repository", e);
    }
  }

  private Map<Long, Job> loadJobsFromFile() {
    Map<Long, Job> jobMap = new HashMap<>();

    if (Files.exists(filePath)) {
      try {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        List<Job> jobs = objectMapper.readValue(filePath.toFile(), new TypeReference<List<Job>>() {});
        for (Job job : jobs) {
          if (job.getId() != null) {
            jobMap.put(job.getId(), job);
            nextId = Math.max(nextId, job.getId() + 1);
          }
        }

      } catch (IOException e) {
        System.err.println("Error reading jobs from file: " + e.getMessage());
        e.printStackTrace();  // You can handle logging or re-throw if necessary
      }
    } else {
      System.err.println("File not found: " + filePath);
    }

    return jobMap;  // Return the populated map
  }


  private void saveJobsToFile(Map<Long, Job> jobMap) {
    try {
      objectMapper.writeValue(filePath.toFile(), jobMap.values());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List<Job> findAll() {
    // Load all jobs from the file each time
    Map<Long, Job> jobMap = loadJobsFromFile();
    return List.copyOf(jobMap.values());
  }

  public Optional<Job> findById(Long id) {
    if (Files.exists(filePath)) {
      try {
        // Use Jackson's ObjectReader to read JSON array incrementally (for efficiency)
        ObjectMapper objectMapper = new ObjectMapper();
        List<Job> jobs = objectMapper.readValue(filePath.toFile(), new TypeReference<>() {});

        // Stream through the list and find the job with the matching ID
        return jobs.stream()
                .filter(job -> job.getId().equals(id))
                .findFirst();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return Optional.empty();
  }

  public Job save(Job job) {
    // Load existing jobs from the file
    Map<Long, Job> jobMap = loadJobsFromFile();
    if (job.getId() == null) {
      job.setId(nextId++);
    }
    jobMap.put(job.getId(), job);
    saveJobsToFile(jobMap);
    return job;
  }

  public void delete(Long id) {
    // Load existing jobs from the file
    Map<Long, Job> jobMap = loadJobsFromFile();
    jobMap.remove(id);
    saveJobsToFile(jobMap);
  }
}
