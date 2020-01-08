package com.virnect.workspace.domain.application;

import com.virnect.workspace.domain.dao.SampleRepository;
import com.virnect.workspace.domain.domain.Sample;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleService {
    private final SampleRepository sampleRepository;

    public String generateNewSampleData(){
        Sample sample = Sample.builder()
                .name(RandomStringUtils.randomAlphabetic(10))
                .build();

        this.sampleRepository.save(sample);
        return sample.getName();
    }
    public String getSampleName(){
        Sample sample = this.sampleRepository.findById(1L).get();
        return sample.getName();
    }
}
