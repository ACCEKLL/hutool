package cn.hutool.core.annotation;

import cn.hutool.core.collection.iter.IterUtil;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * test for {@link RepeatableMetaAnnotatedElement}
 *
 * @author huangchengxing
 */
public class RepeatableMetaAnnotatedElementTest {

	private static final BiFunction<ResolvedAnnotationMapping, Annotation, ResolvedAnnotationMapping> RESOLVED_MAPPING_FACTORY =
		(source, annotation) -> new ResolvedAnnotationMapping(source, annotation, true);

	private static final BiFunction<ResolvedAnnotationMapping, Annotation, ResolvedAnnotationMapping> MAPPING_FACTORY =
		(source, annotation) -> new ResolvedAnnotationMapping(source, annotation, false);

	@Test
	public void testEquals() {
		AnnotatedElement element = RepeatableMetaAnnotatedElement.create(Foo.class, RESOLVED_MAPPING_FACTORY);
		Assert.assertEquals(element, element);
		Assert.assertNotEquals(element, null);
		Assert.assertEquals(element, RepeatableMetaAnnotatedElement.create(Foo.class, RESOLVED_MAPPING_FACTORY));
		Assert.assertNotEquals(element, RepeatableMetaAnnotatedElement.create(Foo.class, MAPPING_FACTORY));
		Assert.assertNotEquals(element, RepeatableMetaAnnotatedElement.create(Annotation1.class, MAPPING_FACTORY));
	}

	@Test
	public void testHashCode() {
		int hashCode = RepeatableMetaAnnotatedElement.create(Foo.class, RESOLVED_MAPPING_FACTORY).hashCode();
		Assert.assertEquals(hashCode, RepeatableMetaAnnotatedElement.create(Foo.class, RESOLVED_MAPPING_FACTORY).hashCode());
		Assert.assertNotEquals(hashCode, RepeatableMetaAnnotatedElement.create(Foo.class, MAPPING_FACTORY).hashCode());
		Assert.assertNotEquals(hashCode, RepeatableMetaAnnotatedElement.create(Annotation1.class, MAPPING_FACTORY).hashCode());
	}

	@Test
	public void testIsAnnotationPresent() {
		AnnotatedElement element = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), Foo.class, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);
		Assert.assertTrue(element.isAnnotationPresent(Annotation1.class));
		Assert.assertTrue(element.isAnnotationPresent(Annotation2.class));
		Assert.assertTrue(element.isAnnotationPresent(Annotation3.class));
		Assert.assertTrue(element.isAnnotationPresent(Annotation4.class));
	}

	@Test
	public void testGetAnnotations() {
		AnnotatedElement element = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), Foo.class, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);
		List<Class<? extends Annotation>> annotationTypes = Arrays.stream(element.getAnnotations())
			.map(Annotation::annotationType)
			.collect(Collectors.toList());
		Map<Class<? extends Annotation>, Integer> countMap = IterUtil.countMap(annotationTypes.iterator());

		Assert.assertEquals((Integer)1, countMap.get(Annotation1.class));
		Assert.assertEquals((Integer)2, countMap.get(Annotation2.class));
		Assert.assertEquals((Integer)4, countMap.get(Annotation3.class));
		Assert.assertEquals((Integer)7, countMap.get(Annotation4.class));
	}

	@Test
	public void testGetAnnotation() {
		AnnotatedElement element = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), Foo.class, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);

		Annotation1 annotation1 = Foo.class.getAnnotation(Annotation1.class);
		Assert.assertEquals(annotation1, element.getAnnotation(Annotation1.class));

		Annotation4 annotation4 = Annotation1.class.getAnnotation(Annotation4.class);
		Assert.assertEquals(annotation4, element.getAnnotation(Annotation4.class));

		Annotation2 annotation2 = annotation1.value()[0];
		Assert.assertEquals(annotation2, element.getAnnotation(Annotation2.class));

		Annotation3 annotation3 = annotation2.value()[0];
		Assert.assertEquals(annotation3, element.getAnnotation(Annotation3.class));
	}

	@Test
	public void testGetAnnotationsByType() {
		AnnotatedElement element = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), Foo.class, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);

		Annotation[] annotations = element.getAnnotationsByType(Annotation1.class);
		Assert.assertEquals(1, annotations.length);

		annotations = element.getAnnotationsByType(Annotation2.class);
		Assert.assertEquals(2, annotations.length);

		annotations = element.getAnnotationsByType(Annotation3.class);
		Assert.assertEquals(4, annotations.length);

		annotations = element.getAnnotationsByType(Annotation4.class);
		Assert.assertEquals(7, annotations.length);
	}

	@Test
	public void testGetDeclaredAnnotations() {
		AnnotatedElement element = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), Foo.class, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);
		List<Class<? extends Annotation>> annotationTypes = Arrays.stream(element.getDeclaredAnnotations())
			.map(Annotation::annotationType)
			.collect(Collectors.toList());
		Map<Class<? extends Annotation>, Integer> countMap = IterUtil.countMap(annotationTypes.iterator());

		Assert.assertEquals((Integer)1, countMap.get(Annotation1.class));
		Assert.assertFalse(countMap.containsKey(Annotation2.class));
		Assert.assertFalse(countMap.containsKey(Annotation3.class));
		Assert.assertFalse(countMap.containsKey(Annotation4.class));
	}

	@Test
	public void testGetDeclaredAnnotation() {
		AnnotatedElement element = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), Foo.class, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);

		Annotation1 annotation1 = Foo.class.getDeclaredAnnotation(Annotation1.class);
		Assert.assertEquals(annotation1, element.getDeclaredAnnotation(Annotation1.class));
		Assert.assertNull(element.getDeclaredAnnotation(Annotation3.class));
		Assert.assertNull(element.getDeclaredAnnotation(Annotation4.class));
		Assert.assertNull(element.getDeclaredAnnotation(Annotation2.class));
	}

	@Test
	public void testGetDeclaredAnnotationsByType() {
		AnnotatedElement element = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), Foo.class, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);

		Annotation[] annotations = element.getDeclaredAnnotationsByType(Annotation1.class);
		Assert.assertEquals(1, annotations.length);

		annotations = element.getDeclaredAnnotationsByType(Annotation2.class);
		Assert.assertEquals(0, annotations.length);

		annotations = element.getDeclaredAnnotationsByType(Annotation3.class);
		Assert.assertEquals(0, annotations.length);

		annotations = element.getDeclaredAnnotationsByType(Annotation4.class);
		Assert.assertEquals(0, annotations.length);
	}

	@Test
	public void testGetElement() {
		AnnotatedElement element = Foo.class;
		RepeatableMetaAnnotatedElement<GenericAnnotationMapping> repeatableMetaAnnotatedElement = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), element, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);
		Assert.assertSame(element, repeatableMetaAnnotatedElement.getElement());
	}

	@Test
	public void testIterator() {
		RepeatableMetaAnnotatedElement<GenericAnnotationMapping> element = RepeatableMetaAnnotatedElement.create(
			RepeatableAnnotationCollector.standard(), Foo.class, (s, a) -> new GenericAnnotationMapping(a, Objects.isNull(s))
		);
		int count = 0;
		for (GenericAnnotationMapping mapping : element) {
			count++;
		}
		Assert.assertEquals(14, count);
	}

	@Annotation4
	@Target(ElementType.TYPE_USE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Annotation1 {
		Annotation2[] value() default {};
	}

	@Annotation4
	@Repeatable(Annotation1.class)
	@Target(ElementType.TYPE_USE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Annotation2 {
		Annotation3[] value() default {};
	}

	@Annotation4
	@Repeatable(Annotation2.class)
	@Target(ElementType.TYPE_USE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Annotation3 { }

	@Target(ElementType.TYPE_USE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Annotation4 { }

	@Annotation1({
		@Annotation2({@Annotation3, @Annotation3}),
		@Annotation2({@Annotation3, @Annotation3})
	})
	private static class Foo {}

}
