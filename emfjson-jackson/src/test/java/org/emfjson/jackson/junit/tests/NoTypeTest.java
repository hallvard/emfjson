/*
 * Copyright (c) 2015 Guillaume Hillairet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Hillairet - initial API and implementation
 */
package org.emfjson.jackson.junit.tests;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;

import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.junit.model.*;
import org.emfjson.jackson.module.EMFModule;
import org.emfjson.jackson.resource.JsonResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NoTypeTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() {
		JacksonOptions options = new JacksonOptions.Builder()
			.withTypes(false)
			.withRoot(ModelPackage.Literals.USER)
			.build();

		EMFModule module = new EMFModule(new ResourceSetImpl(), options);
		mapper.registerModule(module);
	}

	@Test
	public void testSaveSingleObjectWithNoType() {
		JsonNode expected = mapper.createObjectNode()
			.put("userId", "1")
			.put("name", "Paul")
			.put("sex", "MALE");

		User u1 = ModelFactory.eINSTANCE.createUser();
		u1.setUserId("1");
		u1.setName("Paul");

		JsonNode result = mapper.valueToTree(u1);

		assertEquals(expected, result);
	}

	@Test
	public void testSaveTwoRootObjectsWithNoType() {
		JsonNode expected = mapper.createArrayNode()
			.add(mapper.createObjectNode()
				.put("userId", "1")
				.put("name", "Paul")
				.put("sex", "MALE"))
			.add(mapper.createObjectNode()
				.put("userId", "2")
				.put("name", "Anna")
				.put("sex", "FEMALE"));

		User u1 = ModelFactory.eINSTANCE.createUser();
		u1.setUserId("1");
		u1.setName("Paul");

		User u2 = ModelFactory.eINSTANCE.createUser();
		u2.setUserId("2");
		u2.setName("Anna");
		u2.setSex(Sex.FEMALE);

		Resource resource = new JsonResource();
		resource.getContents().add(u1);
		resource.getContents().add(u2);

		JsonNode result = mapper.valueToTree(resource);

		assertEquals(expected, result);
	}

	@Test
	public void testLoadSingleObjectWithNoType() throws JsonProcessingException {
		JsonNode data = mapper.createObjectNode()
			.put("userId", "1")
			.put("name", "Paul")
			.put("sex", "MALE");

		User result = (User) mapper.treeToValue(data, EObject.class);

		assertNotNull(result);

		assertEquals("1", result.getUserId());
		assertEquals("Paul", result.getName());
		assertEquals("MALE", result.getSex().getLiteral());
	}

	@Test
	public void testLoadTwoRootObjectsWithNoType() throws JsonProcessingException {
		JsonNode data = mapper.createArrayNode()
			.add(mapper.createObjectNode()
				.put("userId", "1")
				.put("name", "Paul")
				.put("sex", "MALE"))
			.add(mapper.createObjectNode()
				.put("userId", "2")
				.put("name", "Anna")
				.put("sex", "FEMALE"));

		Resource result = mapper.treeToValue(data, Resource.class);

		assertNotNull(result);
		assertEquals(2, result.getContents().size());

		User first = (User) result.getContents().get(0);
		User second = (User) result.getContents().get(1);

		assertEquals("1", first.getUserId());
		assertEquals("Paul", first.getName());
		assertEquals("MALE", first.getSex().getLiteral());

		assertEquals("2", second.getUserId());
		assertEquals("Anna", second.getName());
		assertEquals("FEMALE", second.getSex().getLiteral());
	}

	@Test
	public void testSaveSingleObjectWithNoTypeAndOneContainment() {
		JsonNode expected = mapper.createObjectNode()
			.put("userId", "1")
			.put("name", "Paul")
			.put("sex", "MALE")
			.set("address", mapper.createObjectNode()
				.put("addId", "a1")
				.put("city", "Prague"));

		User u1 = ModelFactory.eINSTANCE.createUser();
		u1.setUserId("1");
		u1.setName("Paul");

		Address add = ModelFactory.eINSTANCE.createAddress();
		add.setAddId("a1");
		add.setCity("Prague");

		u1.setAddress(add);

		JsonNode result = mapper.valueToTree(u1);

		assertEquals(expected, result);
	}

	@Test
	public void testLoadSingleObjectWithNoTypeAndOneContainment() throws JsonProcessingException {
		JsonNode data = mapper.createObjectNode()
			.put("userId", "1")
			.put("name", "Paul")
			.put("sex", "MALE")
			.set("address", mapper.createObjectNode()
				.put("addId", "a1")
				.put("city", "Prague"));

		User result = (User) mapper.treeToValue(data, EObject.class);

		assertNotNull(result);

		assertEquals("1", result.getUserId());
		assertEquals("Paul", result.getName());
		assertEquals("MALE", result.getSex().getLiteral());

		assertNotNull(result.getAddress());
	}

}
