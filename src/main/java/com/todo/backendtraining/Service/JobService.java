package com.todo.backendtraining.Service;

import com.todo.backendtraining.Entity.Job;
import com.todo.backendtraining.Repository.JobRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JobService {
  private final JobRepository jobRepository;

  public JobService(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  public List<Job> getAllJobs() {
    return jobRepository.findAll();
  }

  public Job getJobById(Long id) {
    return jobRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
  }

  public Job createOrUpdateJob(Job job) {
    return jobRepository.save(job);
  }

  public void deleteJob(Long id) {
    if (!jobRepository.findById(id).isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
    }
    jobRepository.delete(id);
  }
}
