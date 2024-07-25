/*
 * (c) 2003 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license,
 * available at the root application directory.
 */
package org.geonetwork.repository;

import org.geonetwork.domain.GufRatingcriterion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GufRatingcriterionRepository extends JpaRepository<GufRatingcriterion, Integer> {}
