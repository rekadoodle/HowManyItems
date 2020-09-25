package net.minecraft.src.hmi;

public abstract class IC2MPHandler extends TabHandler {

	public abstract boolean isRecipeInput(Object obj);
	public abstract int getId(Object recipeInput);
	public abstract int getDamage(Object recipeInput);
}
