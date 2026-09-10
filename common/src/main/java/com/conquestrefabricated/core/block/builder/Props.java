package com.conquestrefabricated.core.block.builder;

import com.conquestrefabricated.content.loom.WeavingRecipe;
import com.conquestrefabricated.core.Modules;
import com.conquestrefabricated.core.Namespaces;
import com.conquestrefabricated.core.block.data.BlockData;
import com.conquestrefabricated.core.block.data.BlockTemplate;
import com.conquestrefabricated.core.block.data.ColorType;
import com.conquestrefabricated.core.block.factory.BlockFactory;
import com.conquestrefabricated.core.block.factory.InitializationException;
import com.conquestrefabricated.core.block.factory.TypeList;
import com.conquestrefabricated.core.item.family.DeferredFamilyRegistry;
import com.conquestrefabricated.core.item.family.Family;
import com.conquestrefabricated.core.item.family.FamilyFactory;
import com.conquestrefabricated.core.item.family.block.BlockFamily;
import com.conquestrefabricated.core.item.family.block.VariantFamily;
import com.conquestrefabricated.core.util.RenderLayer;
import com.google.common.base.Preconditions;
import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class Props extends BlockProps<Props> implements BlockFactory {

    /**
     * Namespace new builders start out with. Only ever changed, and always restored, by
     * {@link #withDefaultNamespace(String, Runnable)}.
     */
    private static String defaultNamespace = Namespaces.DEFAULT;

    /**
     * Certain Block constructor methods need a BlockState passing to them, ie a 'parent'.
     * For example, Slabs need the full-block instance passing to them to act as the double-slab variant.
     * <p>
     * In cases where the first Block created with the Factory requires a parent Block/BlockState,
     * the 'parent' must be set manually before calling the register(..) methods.
     * <p>
     * We otherwise assume the first Block created with this Factory is the parent.
     * This Block should therefore NOT require a parent Block or BlockState in it's own constructor.
     */
    private BlockState parent = null;
    private BlockName name = null;
    private ColorType colorType = ColorType.NONE;
    private RenderLayer renderLayer = RenderLayer.UNDEFINED;
    private Textures.Builder textures;
    private Map<String, Object> extradata = Collections.emptyMap();
    private FamilyFactory<Block> familyFactory = FamilyFactory.of(BlockFamily::new);
    private Identifier family = null;
    private BlockSetType blockSetType = BlockSetType.OAK;
    private WoodType woodType = WoodType.OAK;
    private Identifier registryId = null;
    private String namespace = defaultNamespace;
    private String module;
    private String namePlural = null;
    private String nameSingular = null;

    private ToolRecipeSpec toolRecipe = null;
    private TimedRecipeSpec weavingRecipe = null;
    private TimedRecipeSpec potteryRecipe = null;
    private PaintingRecipeSpec paintingRecipe = null;

    private List<TagKey<Block>> tags = Collections.emptyList();
    private List<String> lore = Collections.emptyList();

    private boolean manual = false;

    private Props(Block block) {
        super(block);
        // walked once per builder (ie per family), not once per shape
        this.module = Modules.current();
    }

    private Props(Props props) {
        super(props);
        this.name = props.name;
        this.manual = props.manual;
        this.parent = props.parent;
        this.textures = props.textures;
        this.tags = props.tags;
        this.colorType = props.colorType;
        this.extradata = props.extradata;
        this.renderLayer = props.renderLayer;
        this.familyFactory = props.familyFactory;
        this.family = props.family;
        this.namespace = props.namespace;
        this.namePlural = props.namePlural;
        this.nameSingular = props.nameSingular;
        this.lore = props.lore;
        this.toolRecipe = props.toolRecipe;
        this.weavingRecipe = props.weavingRecipe;
        this.potteryRecipe = props.potteryRecipe;
        this.paintingRecipe = props.paintingRecipe;
    }

    @Override
    public void registerItem(BlockData data) {
        registerItemByPlatform(data);
    }

    @ExpectPlatform
    public static void registerItemByPlatform(BlockData data) {
        throw new AssertionError("This method should be replaced by platform implementations!");
    }

    @Override
    public Props getProps() {
        return this;
    }

    //something with light?
    @Override
    protected <T> void applyNonNull(Integer light, Consumer<T> lightLevel) {

    }

    @Override
    public BlockName getName() {
        if (name == null && namePlural != null) {
            // resolved lazily so namespace(..) works anywhere in the chain
            name = BlockName.of(
                    Namespaces.namespaceOf(namePlural, namespace),
                    Namespaces.pathOf(namePlural),
                    Namespaces.pathOf(nameSingular)
            );
        }
        if (name == null) {
            throw new InitializationException("Block name is null");
        }
        return name;
    }

    @Override
    public BlockState getParent() throws InitializationException {
        if (parent == null) {
            throw new InitializationException("Parent state is null");
        }
        return parent;
    }

    @Override
    public Family<Block> createFamily(TypeList types) {
        Identifier name = family == null ? parent == null ? null : BuiltInRegistries.BLOCK.getKey(parent.getBlock()) : family;
        return familyFactory.create(name, group(), types);
    }

    @Override
    public BlockBehaviour.Properties toSettings() throws InitializationException {
        BlockBehaviour.Properties props = super.toSettings();
        if (registryId != null) {
            props.setId(ResourceKey.create(Registries.BLOCK, registryId));
        }
        return props;
    }

    public Props registryId(Identifier id) {
        this.registryId = id;
        return this;
    }

    /**
     * Sets the namespace that unqualified names and textures on this builder resolve against.
     * Defaults to {@link Namespaces#DEFAULT}, so first-party content is unaffected. Third party
     * addons should call this (or use {@link #withDefaultNamespace}, or an explicit
     * {@code namespace:name}) so their content registers under their own id.
     * <p>
     * Names and textures are resolved lazily, so this may be called anywhere in the chain.
     * {@link #family(String)} is the exception: it resolves immediately, so either qualify its
     * argument or call this first.
     */
    public Props namespace(String namespace) {
        this.namespace = namespace;
        Namespaces.register(namespace);
        return this;
    }

    /**
     * Runs {@code registrations} with every {@link Props} it creates defaulting to
     * {@code namespace} instead of {@link Namespaces#DEFAULT}. The previous default is restored
     * afterwards, so an addon can wrap its whole block-init call without touching the submodules
     * that register before or after it.
     */
    public static synchronized void withDefaultNamespace(String namespace, Runnable registrations) {
        Namespaces.register(namespace);
        String previous = defaultNamespace;
        defaultNamespace = namespace;
        try {
            registrations.run();
        } finally {
            defaultNamespace = previous;
        }
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * Overrides which Conquest module this builder's blocks are credited to on the advanced
     * tooltip. Builders normally inherit this from {@link Modules#scope}, so this is only needed
     * for a block registered outside its module's scope.
     */
    public Props module(String moduleId) {
        this.module = moduleId;
        Modules.register(moduleId);
        return this;
    }

    /**
     * @return the module id this builder's blocks are credited to
     */
    public String getModule() {
        return module;
    }

    public Optional<Identifier> getFamily() {
        return Optional.ofNullable(family);
    }

    public ColorType getColorType() {
        return colorType;
    }

    public RenderLayer getRenderLayer() {
        return renderLayer;
    }

    public BlockSetType getBlockSetType() {
        return blockSetType;
    }

    public WoodType getWoodType() {
        return this.woodType;
    }

    public Textures textures() {
        if (textures == null || textures.isEmpty()) {
            return Textures.NONE;
        }
        return textures.build(this::resolveTexture);
    }

    private String resolveTexture(String texture) {
        String textureNamespace = Namespaces.namespaceOf(texture, namespace);
        String path = Namespaces.pathOf(texture);
        if (path.indexOf('/') == -1) {
            path = "block/" + path;
        }
        return withNamespace(textureNamespace, path);
    }

    public boolean isManual() {
        return manual;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public <T> T get(String key, Class<T> type) {
        Object o = extradata.get(key);
        if (o == null) {
            throw new InitializationException(
                    new NullPointerException(key + ": value is null")
            );
        }
        if (!type.isInstance(o)) {
            throw new InitializationException(
                    new ClassCastException(key + ": expected " + type + " but found " + o.getClass())
            );
        }
        return type.cast(o);
    }

    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        Object o = extradata.get(key);
        if (o == null) {
            return defaultValue;
        }
        if (!type.isInstance(o)) {
            throw new InitializationException(
                    new ClassCastException(key + ": expected " + type + " but found " + o.getClass())
            );
        }
        return type.cast(o);
    }

    public Props manual() {
        manual = true;
        return this;
    }

    /**
     * Set the 'parent' (usually the full-block variant) of all subsequent Blocks created by this factory.
     * <p>
     * If not set manually, the first Block instance created by this Factory will be set as the parent. In this case,
     * it's critical that this first Block does not itself require a parent Block/BlockState in it's constructor.
     *
     * @param state The parent BlockState to use
     * @return this Props instance (for chaining calls)
     */
    public Props parent(BlockState state) {
        this.parent = state;
        return this;
    }

    public Props family(String namespace, String name) {
        if (this.parent == null) {
            this.parent = BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath(namespace, name)).get().value().defaultBlockState();
        }
        this.family = Identifier.fromNamespaceAndPath(namespace, name);
        this.familyFactory = DeferredFamilyRegistry.BLOCKS;
        return this;
    }

    public Props family(String name) {
        return family(Namespaces.namespaceOf(name, namespace), Namespaces.pathOf(name));
    }

    public Props name(String namespace, String plural, String singular) {
        return name(BlockName.of(namespace, plural, singular));
    }


    public Props name(String plural, String singular) {
        this.name = null;
        this.namePlural = plural;
        this.nameSingular = singular;
        return this;
    }

    public Props name(String name) {
        return name(name, name);
    }

    public Props name(BlockName name) {
        this.name = name;
        this.namePlural = null;
        this.nameSingular = null;
        return this;
    }

    public Props grassColor() {
        colorType = ColorType.GRASS;
        return this;
    }

    public Props foliageColor() {
        colorType = ColorType.FOLIAGE;
        return this;
    }

    public Props waterColor() {
        colorType = ColorType.WATER;
        return this;
    }

    public Props render(RenderLayer layer) {
        this.renderLayer = layer;
        return this;
    }

    public Props blockSetType(BlockSetType type) {
        this.blockSetType = type;
        return this;
    }

    public Props woodType(WoodType type) {
        this.woodType = type;
        return this;
    }

    public Props texture(String texture) {
        return texture("*", texture);
    }

    public Props texture(String name, String texture) {
        if (textures == null) {
            textures = Textures.builder();
        }

        // Left unqualified here so a later namespace(..) call still applies; resolved in textures().
        textures.add(name, texture);
        return this;
    }

    public Props tag(TagKey<Block> tag) {
        if (tags.isEmpty()) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
        return this;
    }

    @SafeVarargs
    public final Props tags(TagKey<Block>... tags) {
        if (this.tags.isEmpty()) {
            this.tags = new ArrayList<>();
        }
        Collections.addAll(this.tags, tags);
        return this;
    }

    public List<TagKey<Block>> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Adds lore to the item form of every block this builder registers — one line per argument,
     * shown in the tooltip under the block's name.
     * <p>
     * The text given here is the English (en_us) source: the lang datagen writes it out against a
     * generated key, which translators then override. The key belongs to the family as a whole
     * ({@code lore.<namespace>.<name>}), so a {@code TypeList} of cube, slab, stairs and wall
     * produces one lang entry rather than four.
     * <p>
     * This lore comes after anything the block class contributes through
     * {@link com.conquestrefabricated.core.asset.annotation.ItemDescription}, so a block can carry
     * both its shape's stock description and its own flavour text.
     * <p>
     * Calling this more than once appends.
     *
     * @param lines the English lore lines, in the order they should appear
     */
    public Props lore(String... lines) {
        if (lines.length == 0) {
            return this;
        }
        if (lore.isEmpty()) {
            lore = new ArrayList<>(lines.length);
        }
        Collections.addAll(lore, lines);
        return this;
    }

    /**
     * @return the English lore lines set on this builder, in order
     */
    public List<String> getLore() {
        return Collections.unmodifiableList(lore);
    }

    /**
     * Declares that this block is made at a set of crafting tools, from {@code ingredient}.
     * <p>
     * Only the family's parent gets the recipe - the block this builder registers first, or whatever
     * {@link #parent(BlockState)} was pointed at. Every other member of the family is cut from that
     * parent by the stonecutting recipes the core already generates, so one call here is enough for
     * a whole {@code TypeList} of cube, slab, stairs and wall.
     * <p>
     * The tool set is named by id rather than by object so the block builder stays independent of
     * the tools themselves; Conquest's own are {@code CraftingTools.WOODWORKING.id()},
     * {@code CraftingTools.MASON.id()} and {@code CraftingTools.METALWORKING.id()}.
     *
     * <pre>{@code
     * VanillaProps.stone()
     *         .name("granite_ashlar")
     *         .craftedWith(CraftingTools.MASON.id(), Blocks.GRANITE)
     *         .register(types);
     * }</pre>
     *
     * @param tool       id of the tool set that makes this block
     * @param ingredient what goes into the tools' input slot
     * @see RecipeIngredient
     */
    public Props craftedWith(Identifier tool, RecipeIngredient ingredient) {
        return craftedWith(tool, ingredient, 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #craftedWith(Identifier, RecipeIngredient)
     */
    public Props craftedWith(Identifier tool, RecipeIngredient ingredient, int count) {
        this.toolRecipe = ToolRecipeSpec.of(tool, ingredient, count);
        return this;
    }

    /** @see #craftedWith(Identifier, RecipeIngredient) */
    public Props craftedWith(Identifier tool, ItemLike ingredient) {
        return craftedWith(tool, RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #craftedWith(Identifier, RecipeIngredient)
     */
    public Props craftedWith(Identifier tool, ItemLike ingredient, int count) {
        return craftedWith(tool, RecipeIngredient.of(ingredient), count);
    }

    /**
     * Accepts anything in {@code ingredient}, for parents that can be made from a whole family of
     * inputs - all planks, all cobblestones. Takes a tag of either items or blocks; a block tag is
     * usually the handier of the two, since that is how Conquest's own families are grouped.
     *
     * @see #craftedWith(Identifier, RecipeIngredient)
     */
    public Props craftedWith(Identifier tool, TagKey<?> ingredient) {
        return craftedWith(tool, RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #craftedWith(Identifier, TagKey)
     */
    public Props craftedWith(Identifier tool, TagKey<?> ingredient, int count) {
        return craftedWith(tool, RecipeIngredient.of(ingredient), count);
    }

    /**
     * Names the ingredient by id, for the many Conquest blocks that have no static field to point
     * at.
     *
     * @see #craftedWith(Identifier, RecipeIngredient)
     */
    public Props craftedWith(Identifier tool, Identifier ingredient) {
        return craftedWith(tool, RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #craftedWith(Identifier, Identifier)
     */
    public Props craftedWith(Identifier tool, Identifier ingredient, int count) {
        return craftedWith(tool, RecipeIngredient.of(ingredient), count);
    }

    /**
     * Names the ingredient by id. A bare path resolves against {@link Namespaces#DEFAULT}, so
     * {@code "granite_ashlar"} is one of ours and {@code "minecraft:stone"} reaches outside.
     *
     * @see #craftedWith(Identifier, RecipeIngredient)
     */
    public Props craftedWith(Identifier tool, String ingredient) {
        return craftedWith(tool, RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #craftedWith(Identifier, String)
     */
    public Props craftedWith(Identifier tool, String ingredient, int count) {
        return craftedWith(tool, RecipeIngredient.of(ingredient), count);
    }

    /**
     * @return the crafting tool recipe declared for this family, if any
     */
    public Optional<ToolRecipeSpec> getToolRecipe() {
        return Optional.ofNullable(toolRecipe);
    }

    /**
     * Declares that this block is woven at a loom, from {@code ingredient}.
     * <p>
     * The same family rule as {@link #craftedWith} applies: only the parent gets the recipe, and
     * every other member is reached from it - here through the loom picker's family toggle, which
     * shapes a finished cloth into its layers and slabs for nothing.
     * <p>
     * Weaving takes time, which is what separates a loom from a set of crafting tools. Leave the
     * time out for the default of {@value com.conquestrefabricated.content.loom.WeavingRecipe#DEFAULT_TIME}
     * ticks.
     *
     * <pre>{@code
     * VanillaProps.cloth()
     *         .name("red_canvas")
     *         .woven(Blocks.RED_WOOL)
     *         .register(types);
     * }</pre>
     *
     * @param ingredient what goes into the loom's input slot
     * @see RecipeIngredient
     */
    public Props woven(RecipeIngredient ingredient) {
        return woven(ingredient, 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #woven(RecipeIngredient)
     */
    public Props woven(RecipeIngredient ingredient, int count) {
        return woven(ingredient, count, WeavingRecipe.DEFAULT_TIME);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #woven(RecipeIngredient)
     */
    public Props woven(RecipeIngredient ingredient, int count, int time) {
        this.weavingRecipe = TimedRecipeSpec.of(ingredient, count, time);
        return this;
    }

    /** @see #woven(RecipeIngredient) */
    public Props woven(ItemLike ingredient) {
        return woven(RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #woven(RecipeIngredient)
     */
    public Props woven(ItemLike ingredient, int count) {
        return woven(RecipeIngredient.of(ingredient), count);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #woven(RecipeIngredient)
     */
    public Props woven(ItemLike ingredient, int count, int time) {
        return woven(RecipeIngredient.of(ingredient), count, time);
    }

    /**
     * Accepts anything in {@code ingredient}, for cloths that can be woven from a whole family of
     * inputs - any wool, any plant fibre. Takes a tag of either items or blocks.
     *
     * @see #woven(RecipeIngredient)
     */
    public Props woven(TagKey<?> ingredient) {
        return woven(RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #woven(TagKey)
     */
    public Props woven(TagKey<?> ingredient, int count) {
        return woven(RecipeIngredient.of(ingredient), count);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #woven(TagKey)
     */
    public Props woven(TagKey<?> ingredient, int count, int time) {
        return woven(RecipeIngredient.of(ingredient), count, time);
    }

    /**
     * Names the ingredient by id, for the many Conquest blocks that have no static field to point
     * at.
     *
     * @see #woven(RecipeIngredient)
     */
    public Props woven(Identifier ingredient) {
        return woven(RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #woven(Identifier)
     */
    public Props woven(Identifier ingredient, int count) {
        return woven(RecipeIngredient.of(ingredient), count);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #woven(Identifier)
     */
    public Props woven(Identifier ingredient, int count, int time) {
        return woven(RecipeIngredient.of(ingredient), count, time);
    }

    /**
     * Names the ingredient by id. A bare path resolves against {@link Namespaces#DEFAULT}.
     *
     * @see #woven(RecipeIngredient)
     */
    public Props woven(String ingredient) {
        return woven(RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #woven(String)
     */
    public Props woven(String ingredient, int count) {
        return woven(RecipeIngredient.of(ingredient), count);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #woven(String)
     */
    public Props woven(String ingredient, int count, int time) {
        return woven(RecipeIngredient.of(ingredient), count, time);
    }

    /**
     * @return the weaving recipe declared for this family, if any
     */
    public Optional<TimedRecipeSpec> getWeavingRecipe() {
        return Optional.ofNullable(weavingRecipe);
    }

    /**
     * Declares that this block is thrown on a pottery wheel, from {@code ingredient}.
     * <p>
     * The same family rule as {@link #craftedWith} applies: only the parent gets the recipe, and
     * every other member is reached from it - here through the wheel picker's family toggle, which
     * cuts a finished pot into its shapes for nothing.
     * <p>
     * Throwing takes time, which is what separates a wheel from a set of crafting tools. Leave the
     * time out for the default of {@value com.conquestrefabricated.content.loom.WeavingRecipe#DEFAULT_TIME}
     * ticks.
     *
     * <pre>{@code
     * VanillaProps.stone()
     *         .name("terracotta_amphora")
     *         .thrown(Items.CLAY_BALL)
     *         .register(types);
     * }</pre>
     *
     * @param ingredient what goes into the wheel's input slot
     * @see RecipeIngredient
     */
    public Props thrown(RecipeIngredient ingredient) {
        return thrown(ingredient, 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #thrown(RecipeIngredient)
     */
    public Props thrown(RecipeIngredient ingredient, int count) {
        return thrown(ingredient, count, WeavingRecipe.DEFAULT_TIME);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #thrown(RecipeIngredient)
     */
    public Props thrown(RecipeIngredient ingredient, int count, int time) {
        this.potteryRecipe = TimedRecipeSpec.of(ingredient, count, time);
        return this;
    }

    /** @see #thrown(RecipeIngredient) */
    public Props thrown(ItemLike ingredient) {
        return thrown(RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #thrown(RecipeIngredient)
     */
    public Props thrown(ItemLike ingredient, int count) {
        return thrown(RecipeIngredient.of(ingredient), count);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #thrown(RecipeIngredient)
     */
    public Props thrown(ItemLike ingredient, int count, int time) {
        return thrown(RecipeIngredient.of(ingredient), count, time);
    }

    /**
     * Accepts anything in {@code ingredient}, for cloths that can be thrown from a whole family of
     * inputs - any wool, any plant fibre. Takes a tag of either items or blocks.
     *
     * @see #thrown(RecipeIngredient)
     */
    public Props thrown(TagKey<?> ingredient) {
        return thrown(RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #thrown(TagKey)
     */
    public Props thrown(TagKey<?> ingredient, int count) {
        return thrown(RecipeIngredient.of(ingredient), count);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #thrown(TagKey)
     */
    public Props thrown(TagKey<?> ingredient, int count, int time) {
        return thrown(RecipeIngredient.of(ingredient), count, time);
    }

    /**
     * Names the ingredient by id, for the many Conquest blocks that have no static field to point
     * at.
     *
     * @see #thrown(RecipeIngredient)
     */
    public Props thrown(Identifier ingredient) {
        return thrown(RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #thrown(Identifier)
     */
    public Props thrown(Identifier ingredient, int count) {
        return thrown(RecipeIngredient.of(ingredient), count);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #thrown(Identifier)
     */
    public Props thrown(Identifier ingredient, int count, int time) {
        return thrown(RecipeIngredient.of(ingredient), count, time);
    }

    /**
     * Names the ingredient by id. A bare path resolves against {@link Namespaces#DEFAULT}.
     *
     * @see #thrown(RecipeIngredient)
     */
    public Props thrown(String ingredient) {
        return thrown(RecipeIngredient.of(ingredient), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #thrown(String)
     */
    public Props thrown(String ingredient, int count) {
        return thrown(RecipeIngredient.of(ingredient), count);
    }

    /**
     * @param time how many ticks one craft takes
     * @see #thrown(String)
     */
    public Props thrown(String ingredient, int count, int time) {
        return thrown(RecipeIngredient.of(ingredient), count, time);
    }

    /**
     * @return the pottery recipe declared for this family, if any
     */
    public Optional<TimedRecipeSpec> getPotteryRecipe() {
        return Optional.ofNullable(potteryRecipe);
    }

    /**
     * Declares that this block is made with a painter's kit, from {@code base} coloured with
     * {@code paint}.
     * <p>
     * The same family rule as {@link #craftedWith} applies: only the parent gets the recipe. The rest
     * of the family is reached through the picker's family toggle, and reshaping a painted block
     * costs no paint - a kit will cut the slabs of a stucco it made, but will not cut the raw
     * cobblestone it paints onto.
     *
     * <pre>{@code
     * VanillaProps.stone()
     *         .name("red_stucco")
     *         .painted(Blocks.COBBLESTONE, Items.RED_DYE)
     *         .register(types);
     * }</pre>
     *
     * <p>Only the commonest shapes have overloads of their own; wrap anything else with
     * {@link RecipeIngredient#of}, which covers tags, ids and bare strings alike.</p>
     *
     * @param base  what goes into the kit's upper slot - the material being painted
     * @param paint what goes into the lower slot - the dye, lime or wash it is painted with
     */
    public Props painted(RecipeIngredient base, RecipeIngredient paint) {
        return painted(base, paint, 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #painted(RecipeIngredient, RecipeIngredient)
     */
    public Props painted(RecipeIngredient base, RecipeIngredient paint, int count) {
        this.paintingRecipe = PaintingRecipeSpec.of(base, paint, count);
        return this;
    }

    /** @see #painted(RecipeIngredient, RecipeIngredient) */
    public Props painted(ItemLike base, ItemLike paint) {
        return painted(RecipeIngredient.of(base), RecipeIngredient.of(paint), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #painted(RecipeIngredient, RecipeIngredient)
     */
    public Props painted(ItemLike base, ItemLike paint, int count) {
        return painted(RecipeIngredient.of(base), RecipeIngredient.of(paint), count);
    }

    /**
     * Paints anything in {@code base}, which is usually how a whole family of materials is named.
     *
     * @see #painted(RecipeIngredient, RecipeIngredient)
     */
    public Props painted(TagKey<?> base, ItemLike paint) {
        return painted(RecipeIngredient.of(base), RecipeIngredient.of(paint), 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #painted(TagKey, ItemLike)
     */
    public Props painted(TagKey<?> base, ItemLike paint, int count) {
        return painted(RecipeIngredient.of(base), RecipeIngredient.of(paint), count);
    }

    /**
     * @return the painting recipe declared for this family, if any
     */
    public Optional<PaintingRecipeSpec> getPaintingRecipe() {
        return Optional.ofNullable(paintingRecipe);
    }

    public Props template(BlockTemplate template) {
        if (getRenderLayer().isCutout() || template.getRenderLayer().isCutout()) {
            Props props = new Props(this);
            props.solid(false);
            return props;
        }
        return this;
    }

    public Props with(String key, Object data) {
        if (extradata.isEmpty()) {
            extradata = new HashMap<>();
        }
        extradata.put(key, data);
        return this;
    }

    public Props variantFamily() {
        familyFactory = FamilyFactory.of(VariantFamily::new);
        return this;
    }

    public static Props create(Block block) {
        Preconditions.checkNotNull(block, "Block must not be null");
        return new Props(block);
    }

    public static Props create(BlockState state) {
        Preconditions.checkNotNull(state, "BlockState must not be null");
        return create(state.getBlock());
    }

    /*
    public static Props create(MapColor color) {
        Preconditions.checkNotNull(color, "MaterialColor must not be null");
        return new Props(color);
    }*/

    private static String withNamespace(String namespace, String name) {
        if (name.indexOf(':') != -1) {
            return name;
        }
        return namespace + ':' + name;
    }
}
