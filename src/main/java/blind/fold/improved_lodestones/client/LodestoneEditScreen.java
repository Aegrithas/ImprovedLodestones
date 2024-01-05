package blind.fold.improved_lodestones.client;

import blind.fold.improved_lodestones.LodestoneBlockEntity;
import blind.fold.improved_lodestones.UpdateLodestoneC2SPacket;
import blind.fold.improved_lodestones.LodestoneState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static blind.fold.improved_lodestones.LodestoneState.Named.MAX_NAME_LENGTH;

@Environment(EnvType.CLIENT)
public class LodestoneEditScreen extends Screen {
  
  private static final Text LABEL_TEXT = Text.translatable("lodestoneScreen.title");
  
  private static final int AVAILABLE_NAME_COLOR = 0xFFFFFF;
  
  private static final int ANONYMOUS_NAME_COLOR = 0xAAAAAA;
  
  private static final int TAKEN_NAME_COLOR = 0xFF5555;
  
  private static final Text AVAILABLE_NAME_TEXT = Text.translatable("lodestoneScreen.availability.available").setStyle(Style.EMPTY.withColor(AVAILABLE_NAME_COLOR));
  
  private static final Text ANONYMOUS_NAME_TEXT = Text.translatable("lodestoneScreen.availability.anonymous").setStyle(Style.EMPTY.withColor(ANONYMOUS_NAME_COLOR));
  
  private static final Text TAKEN_NAME_TEXT = Text.translatable("lodestoneScreen.availability.taken").setStyle(Style.EMPTY.withColor(TAKEN_NAME_COLOR));
  
  private final LodestoneBlockEntity blockEntity;
  
  private boolean available;
  
  protected ButtonWidget doneButton;
  
  protected ButtonWidget cancelButton;
  
  protected TextFieldWidget nameTextField;
  
  protected TextWidget nameAvailabilityLabel;
  
  public LodestoneEditScreen(LodestoneBlockEntity blockEntity) {
    super(LABEL_TEXT);
    this.blockEntity = blockEntity;
  }
  
  @Override
  protected void init() {
    this.doneButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.finishEditing()).dimensions(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
    this.nameTextField = this.addDrawableChild(new  TextFieldWidget(this.textRenderer, this.width / 2 - 150, 50, 300, 20, Text.translatable("lodestoneScreen.name")));
    this.nameTextField.setMaxLength(MAX_NAME_LENGTH);
    var state = this.blockEntity.getState().orElse(new LodestoneState.Anonymous());
    this.nameTextField.setText(state.name());
    this.available = true;
    this.nameTextField.setEditableColor(AVAILABLE_NAME_COLOR);
    this.nameTextField.setChangedListener(this::onNameChanged);
    this.setInitialFocus(this.nameTextField);
    this.nameAvailabilityLabel = this.addDrawableChild(new TextWidget(this.width / 2 - 150, 70, 300, 20, state instanceof LodestoneState.Anonymous ? ANONYMOUS_NAME_TEXT : AVAILABLE_NAME_TEXT, this.textRenderer)).alignLeft();
  }
  
  private void onNameChanged(String name) {
    var state = LodestoneState.Existing.forName(name);
    this.available = this.blockEntity.isStateAvailable(state) || this.blockEntity.getState().filter(currentState -> currentState.equals(state)).isPresent();
    this.nameTextField.setEditableColor(this.available ? AVAILABLE_NAME_COLOR : TAKEN_NAME_COLOR);
    this.nameAvailabilityLabel.setMessage(this.available ? state instanceof LodestoneState.Anonymous ? ANONYMOUS_NAME_TEXT : AVAILABLE_NAME_TEXT : TAKEN_NAME_TEXT);
  }
  
  @Override
  public void tick() {
    super.tick();
    this.nameTextField.tick();
    if (!this.canEdit()) {
      this.close();
    }
  }
  
  private boolean canEdit() {
    return this.client != null && this.client.player != null && !this.blockEntity.isRemoved() && !this.blockEntity.isPlayerTooFarToEdit(this.client.player.getUuid());
  }
  
  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (super.keyPressed(keyCode, scanCode, modifiers)) {
      return true;
    }
    if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
      this.finishEditing();
      return true;
    }
    return false;
  }
  
  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    this.renderBackground(context);
    context.drawCenteredTextWithShadow(this.textRenderer, LABEL_TEXT, this.width / 2, 20, 0xFFFFFF);
    super.render(context, mouseX, mouseY, delta);
  }
  
  @Override
  public void close() {
    this.finishEditing();
  }
  
  @Override
  public void removed() {
    if (!this.available) return;
    assert this.client != null;
    var networkHandler = this.client.getNetworkHandler();
    if (networkHandler != null) networkHandler.sendPacket(new UpdateLodestoneC2SPacket(this.blockEntity.getPos(), LodestoneState.Existing.forName(nameTextField.getText())));
  }
  
  @Override
  public boolean shouldPause() {
    return false;
  }
  
  protected void finishEditing() {
    assert this.client != null;
    this.client.setScreen(null);
  }
  
}
