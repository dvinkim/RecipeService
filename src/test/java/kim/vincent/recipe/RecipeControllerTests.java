package kim.vincent.recipe;

import java.util.ArrayList;

import org.junit.Test;

import junit.framework.TestCase;

public class RecipeControllerTests extends TestCase {
	
	@Test
	public void testLogic() {
		RecipeController rc = new RecipeController();
		
		ArrayList<String> recipe = rc.getRecipe("b", "c", "c");
		assertEquals(recipe.get(0), "We've got a recipe for you:");
		assertEquals(recipe.get(1), "World's best Lasagna");
		assertEquals(recipe.get(2), "You've got all the time in the world - enjoy the process!");
		assertEquals(recipe.get(3), "Food is a labor of love - your family will appreciate it!");
		
		recipe = rc.getRecipe("a", "a", "a");
		assertEquals(recipe.get(0), "Let's make something quick:");
		assertEquals(recipe.get(1), "Tortellini with basil");
		assertEquals(recipe.get(2), "We know you don't have a lot of time, so we created this recipe for you.");
		assertEquals(recipe.get(3), "Hopefully we saved you some time - we know you're busy!");
		
		recipe = rc.getRecipe("a", "a", "c");
		assertEquals(recipe.get(0), "Healthy vegetarian options for you!");
		assertEquals(recipe.get(1), "Tortellini with basil");
		assertEquals(recipe.get(2), "You've got all the time in the world - enjoy the process!");
		assertEquals(recipe.get(3), "Enjoy your meal!");
		
		recipe = rc.getRecipe("b", "a", "b");
		assertEquals(recipe.get(0), "We've got a recipe for you:");
		assertEquals(recipe.get(1), "Busy spaghetti");
		assertEquals(recipe.get(2), "Remember to keep an eye on the clock - this should take you roughly half an hour.");
		assertEquals(recipe.get(3), "Enjoy your meal!");
		
		recipe = rc.getRecipe("b", "b", "a");
		assertEquals(recipe.get(0), "Let's make something quick:");
		assertEquals(recipe.get(1), "Busy spaghetti");
		assertEquals(recipe.get(2), "We know you don't have a lot of time, so we created this recipe for you.");
		assertEquals(recipe.get(3), "Enjoy your meal!");
	}

}
