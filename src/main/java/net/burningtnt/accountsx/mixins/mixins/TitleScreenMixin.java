package net.burningtnt.accountsx.mixins.mixins;

import net.burningtnt.accountsx.AccountsX;
import net.burningtnt.accountsx.config.AccountManager;
import net.burningtnt.accountsx.gui.AccountScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Unique
    private static final Identifier SWITCH_ACCOUNT_ICON_TEXTURE = new Identifier(AccountsX.MOD_ID, "icon/account");

    @Final
    @Shadow
    private boolean doBackgroundFade;

    @Shadow
    private long backgroundFadeStart;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgetsNormal", at = @At("RETURN"))
    protected void init(CallbackInfo ci) {
        assert this.client != null;
        int j = this.height / 4 + 48;

        this.addDrawableChild(TextIconButtonWidget.builder(
                        Text.translatable("as.account.action.add_account"),
                        (button) -> this.client.setScreen(new AccountScreen(this)),
                        true)
                .dimension(20, 20)
                .texture(SWITCH_ACCOUNT_ICON_TEXTURE, 20, 20)
                .build()
        ).setPosition(this.width / 2 + 104, j + 24 * 2);
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        float f = this.doBackgroundFade ? (float) (Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int i = MathHelper.ceil(g * 255.0F) << 24;

        if ((i & -67108864) != 0) {
            context.drawCenteredTextWithShadow(this.textRenderer, AccountManager.getCurrentAccountInfoText(), this.width / 2, 15, 0xFFFFFF | i);
        }
    }
}
