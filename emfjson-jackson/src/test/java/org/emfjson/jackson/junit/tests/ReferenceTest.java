/*
 * Copyright (c) 2015 Guillaume Hillairet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Hillairet - initial API and implementation
 *
 */
package org.emfjson.jackson.junit.tests;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;

import org.emfjson.EMFJs;
import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.databind.deser.references.ReferenceAsValueDeserializer;
import org.emfjson.jackson.databind.ser.references.ReferenceAsValueSerializer;
import org.emfjson.jackson.junit.model.ModelFactory;
import org.emfjson.jackson.junit.model.ModelPackage;
import org.emfjson.jackson.junit.model.Sex;
import org.emfjson.jackson.junit.model.User;
import org.emfjson.jackson.junit.support.TestSupport;
import org.emfjson.jackson.module.EMFModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class ReferenceTest extends TestSupport {

	@Test
	public void testSaveTwoObjectsWithAttributesOneReference() throws IOException {
		JsonNode expected = mapper.createArrayNode()
			.add(mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("userId", "1")
				.put("name", "John")
				.put("sex", "MALE")
				.set("uniqueFriend", mapper.createObjectNode()
						.put("$ref", "2")
				))
			.add(mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("userId", "2")
				.put("name", "Mary")
				.put("sex", "FEMALE"));

		User user1 = ModelFactory.eINSTANCE.createUser();
		user1.setUserId("1");
		user1.setName("John");

		User user2 = ModelFactory.eINSTANCE.createUser();
		user2.setUserId("2");
		user2.setName("Mary");
		user2.setSex(Sex.FEMALE);

		user1.setUniqueFriend(user2);

		Resource resource = resourceSet.createResource(URI.createURI("test"));

		resource.getContents().add(user1);
		resource.getContents().add(user2);

		assertEquals(expected, mapper.valueToTree(resource));
	}

	@Test
	public void testLoadTwoObjectsWithAttributesOneReference() throws IOException {
		JsonNode data = mapper.createArrayNode()
			.add(mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("userId", "1")
				.put("name", "John")
				.put("sex", "MALE")
				.set("uniqueFriend", mapper.createObjectNode()
						.put("$ref", "2")
				))
			.add(mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("userId", "2")
				.put("name", "Mary")
				.put("sex", "FEMALE"));

		Resource resource = resourceSet.createResource(URI.createURI("tests/test-save-3.json"));
		resource.load(new ByteArrayInputStream(mapper.writeValueAsBytes(data)), null);

		assertEquals(2, resource.getContents().size());

		User first = (User) resource.getContents().get(0);
		User second = (User) resource.getContents().get(1);

		assertSame(second, first.getUniqueFriend());
	}

	@Test
	public void testLoadWithExternalReferenceFromFile() throws IOException {
		JsonNode data = mapper.createObjectNode()
			.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
			.put("userId", "2")
			.set("uniqueFriend", mapper.createObjectNode()
				.put("$ref", "http://eclipselabs.org/emfjson/tests/test-load-1.json#1"));

		Resource resource = resourceSet.createResource(URI.createURI("http://resources/second"));
		resource.load(new ByteArrayInputStream(mapper.writeValueAsBytes(data)), options);

		assertFalse(resource.getContents().isEmpty());

		User u2 = (User) resource.getContents().get(0);

		assertNotNull(u2.getUniqueFriend());
		assertFalse(u2.getUniqueFriend().eIsProxy());
	}

	@Test
	public void testLoadWithExternalReference() throws IOException {
		JsonNode firstResource = mapper.createObjectNode()
			.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
			.put("userId", "1");

		JsonNode secondResource = mapper.createObjectNode()
			.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
			.put("userId", "2")
			.set("uniqueFriend", mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("$ref", "http://resources/first#1"));

		Resource first = resourceSet.createResource(URI.createURI("http://resources/first"));
		Resource second = resourceSet.createResource(URI.createURI("http://resources/second"));

		first.load(new ByteArrayInputStream(mapper.writeValueAsBytes(firstResource)), options);
		second.load(new ByteArrayInputStream(mapper.writeValueAsBytes(secondResource)), options);

		assertFalse(first.getContents().isEmpty());
		assertFalse(second.getContents().isEmpty());

		User u1 = (User) first.getContents().get(0);
		User u2 = (User) second.getContents().get(0);

		assertSame(u1, u2.getUniqueFriend());
	}

	@Test
	public void testLoadThreeObjectsTwoReferences() throws IOException {
		options.put(EMFJs.OPTION_ROOT_ELEMENT, ModelPackage.Literals.USER);

		Resource resource = resourceSet.createResource(uri("test-load-5.json"));
		assertNotNull(resource);

		resource.load(options);

		assertFalse(resource.getContents().isEmpty());
		assertEquals(3, resource.getContents().size());

		EObject obj1 = resource.getContents().get(0);
		assertTrue(obj1 instanceof User);
		assertEquals("1", ((User) obj1).getUserId());

		EObject obj2 = resource.getContents().get(1);
		assertTrue(obj2 instanceof User);
		assertEquals("2", ((User) obj2).getUserId());

		EObject obj3 = resource.getContents().get(2);
		assertTrue(obj3 instanceof User);
		assertEquals("3", ((User) obj3).getUserId());

		assertFalse(((User) obj1).getFriends().isEmpty());
		assertEquals(2, ((User) obj1).getFriends().size());

		User friend1 = ((User) obj1).getFriends().get(0);
		assertNotNull(friend1);

		User friend2 = ((User) obj1).getFriends().get(1);
		assertNotNull(friend2);

		assertFalse(friend1.eIsProxy());
		assertFalse(friend2.eIsProxy());

		assertEquals(obj2, friend1);
		assertEquals(obj3, friend2);
	}

	@Test
	public void testSaveReferenceAsValue() {
		ObjectMapper mapper = new ObjectMapper();
		JacksonOptions opts = new JacksonOptions.Builder()
			.withReferenceSerializer(new ReferenceAsValueSerializer())
			.build();
		mapper.registerModule(new EMFModule(resourceSet, opts));

		JsonNode expected = mapper.createArrayNode()
			.add(mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("userId", "1")
				.put("name", "John")
				.put("sex", "MALE")
				.put("uniqueFriend", "2"))
			.add(mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("userId", "2")
				.put("name", "Mary")
				.put("sex", "FEMALE"));

		User user1 = ModelFactory.eINSTANCE.createUser();
		user1.setUserId("1");
		user1.setName("John");

		User user2 = ModelFactory.eINSTANCE.createUser();
		user2.setUserId("2");
		user2.setName("Mary");
		user2.setSex(Sex.FEMALE);

		user1.setUniqueFriend(user2);

		Resource resource = resourceSet.createResource(URI.createURI("test"));
		resource.getContents().add(user1);
		resource.getContents().add(user2);

		assertEquals(expected, mapper.valueToTree(resource));
	}

	@Test
	public void testLoadReferenceAsValue() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JacksonOptions opts = new JacksonOptions.Builder()
			.withReferenceDeserializer(new ReferenceAsValueDeserializer())
			.build();
		mapper.registerModule(new EMFModule(resourceSet, opts));

		JsonNode data = mapper.createArrayNode()
			.add(mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("userId", "1")
				.put("name", "John")
				.put("sex", "MALE")
				.put("uniqueFriend", "2"))
			.add(mapper.createObjectNode()
				.put("eClass", "http://www.eclipselabs.org/emfjson/junit#//User")
				.put("userId", "2")
				.put("name", "Mary")
				.put("sex", "FEMALE"));

		Resource resource = mapper.treeToValue(data, Resource.class);

		assertEquals(2, resource.getContents().size());

		User first = (User) resource.getContents().get(0);
		User second = (User) resource.getContents().get(1);

		assertSame(second, first.getUniqueFriend());
	}

}