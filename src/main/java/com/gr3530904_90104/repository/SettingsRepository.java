package com.gr3530904_90104.repository;

import com.gr3530904_90104.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Integer> {
    Optional<Settings> findSettingsById(Integer id);
    Optional<Settings> findSettingsByUserId(Integer id);
}
