package com.conquestrefabricated.core.block.builder;

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
import net.minecraft.world.item.Item;
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
    private String namePlural = null;
    private String nameSingular = null;

    private ToolRecipeSpec toolRecipe = null;

    private List<TagKey<Block>> tags = Collections.emptyList();
    private List<String> lore = Collections.emptyList();

    private boolean manual = false;

    private Props(Block block) {
        super(block);
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
     * the tools themselves; Conquest's own are
     * {@code CraftingTools.WOODWORKING.id()} and {@code CraftingTools.MASON.id()}.
     *
     * <pre>{@code
     * VanillaProps.stone()
     *         .name("granite_ashlar")
     *         .craftedWith(CraftingTools.MASON.id(), Blocks.GRANITE)
     *         .register(types);
     * }</pre>
     *
     * @param tool       id of the tool set that makes this block
     * @param ingredient the block or item that goes into the tools' input slot
     */
    public Props craftedWith(Identifier tool, ItemLike ingredient) {
        return craftedWith(tool, ingredient, 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #craftedWith(Identifier, ItemLike)
     */
    public Props craftedWith(Identifier tool, ItemLike ingredient, int count) {
        this.toolRecipe = ToolRecipeSpec.of(tool, ingredient, count);
        return this;
    }

    /**
     * Accepts anything in {@code ingredient}, for parents that can be made from a whole family of
     * inputs - all planks, all cobblestones.
     *
     * @see #craftedWith(Identifier, ItemLike)
     */
    public Props craftedWith(Identifier tool, TagKey<Item> ingredient) {
        return craftedWith(tool, ingredient, 1);
    }

    /**
     * @param count how many the recipe yields
     * @see #craftedWith(Identifier, TagKey)
     */
    public Props craftedWith(Identifier tool, TagKey<Item> ingredient, int count) {
        this.toolRecipe = ToolRecipeSpec.of(tool, ingredient, count);
        return this;
    }

    /**
     * @return the crafting tool recipe declared for this family, if any
     */
    public Optional<ToolRecipeSpec> getToolRecipe() {
        return Optional.ofNullable(toolRecipe);
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
