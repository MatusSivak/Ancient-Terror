package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Base64Coder;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.music.NewMusicBox;
import sk.sivak.eldritchhorror.core.view.utils.ButtonUtils;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.getTextureRegionDrawable;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class MenuButton extends ImageButton {

    private final Preferences preferences;
    private RestartGameAction restartGameAction;
    private boolean restartButtonDisplayed;
    boolean menuButtonClickable = true;
    private Skin skin;
    private final static String BUG_BUTTON_DATA = "iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAYAAAD0eNT6AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAN1wAADdcBQiibeAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAD+WSURBVHja7Z0HlFVF1qjv7RzonICmczeZRnIQhgyKWURAcBTMio4RMCJGEFFHRpIg5jGOOuogOOasv2lMM4YxDIoBFTGgGOrf1Z5WUkN333vr1Dnn67W+td56b9aTW1V77++cU7UrpJQKAQAAQLBgEAAAABAAAAAAQAAAAAAAAQAAAAAEAAAAABAAAAAAQAAAAAAAAQAAAAAEAAAAABAAAAAAQAAAAAAAAQAAAAAEAAAAABAAAAAAQAAAAAAAAQAAAAAEAAAAABAAAAAABAAAAAAQAAAAAEAAAAAAAAEAAAAABADAP0ERCuUKXYXRDvr/nMvYeGoOU4S2whBhb6GfUCrEMT4ACADApgVDF4eZwouCaoAXhLP0/5Yxs3IOE4Upwt3Chgbm8GNhiTCYMQMEgEEACscRwvrtFP4t0f/bIxg7q+awm/ByE+ZQc7WQzfgBAgAQzKf+VU0sGpuyircBVjz1ny382Mw5XC3swlgCAgAQnMKxTxOf+rf3NmAfxtSVOSwWXorCHGr+wpgCAgAQjCf/9VEqHPUSwJsA8/O4IopzqJnCuAICAODvwrEqyoWj7nMAY2t0Dg+JwRx+hcgBAgDg7w1/KkawMdDMHJY4xToWc3i/EGacAQEA4NU/nwLsm8f7YjiHmiMZZ0AAAPxVOGbGuHBoZjLWMZ3Ddgbm8A3GGhAAAH8VjxcNFI8XGeuYzuF0A3Ooacd4AwIA4I/CkWuocCjaBntuA+e2OIrxBgQAwB+Fo6tBAejKmMdsHt8wNIfnMd6AAAD4o3CMNigAoxnzmM3jekNzuJTxBgQAwB+FY7xBARjPmMdsHk3N4U2MNyAAAAgAAoAAACAAAAgAAoAAACAAAAgAAoAAACAAAAgAAoAAACAAAAgAIAAACAAAAgAIAAACAIAAAAIAgAAAIACAAAAgAAAIACAAAAgAAAIACAAAAgDQ1MIxDgFAAJrAXxlvQAAA/FE49jJYPLhKNjZzmGNwDpcz5oAAAPijeAwxWDy4SjY2c9jZ4BzOZ8wBAQDwR/HoYbB4XMWYx2QORxqcwwsZc0AAAPxRPNoaLB4rGfOYzOFkg3N4OmMOCACAP4pHS4PF4xXGPCZzeLrBOTyOMQcEAMAfxSPNYPH4gjGPyRwuMDiHkxlzQAAA/FNAPjFYQEoY86jP3+MG528wYw4IAIB/CsgjBgvIsYx51D/h/Gxw/ooYd0AAAPxTRJYYLCAPMeZRnbsj+IQDgAAANLeInGSwiPwk5DPuUZu7lQbn7gnGHBAAAH8Vkd0MFhHNFMY9KvOWLWw0OG9LGXdAAAD8VUjKDAvAPYx7VOZtkuF5+xPjDggAgP+KyX8NFpIfhXaMe0TzFRaeNSwAOzH2gAAA+K+gXGm4mNzFuEc0XxMNz9fnWjoYe0AAAPxXUMYbLiiaQYx9s+YqVfjA8FzdztgDAgDgz6JS5IIA/B9Plc2aq9NdmKupjD0gAAD+LSyvuFBYJjH2TRa1r12Yp46MPyAAAP4tLme7UFhW012uSXN0qwtz9AZjDwgAgL+LS5ULxUXzjP6uzRxYKWiaGYw/IAAA/i8yT7hUZG5lP8B252WCS/Oi7xlowxwAAgDg/0JzpEuFRnMBc7DNOeknbHBpTu5nDgABAAhGsckRfnBRArhvfvP5KDd8XTObNIG4YxAgwEXnehcLjr4saBrzUDcPvZ1Nkm7NxafszQAEACBYhaej8IuLhUdzk5AW4DmYInzv8hycSjwAAgAQvAJ0u8vFR/OyUBGwcU8UrrBg7L8QMokFQAAAgicA3SwoQvU96HcLyJiXCI9aMu6ziANAAACCKwH3WlKMNA8IfXw6zgXCJRa88q9nvZBLDAACABBcAejunANXFnGH0Mkn45upn7Rdau27Pc5m/QMCAIAE/MWy4lTfnOZafZugEO/BMa0WThPWWji27wgprH1AAAAQgCxhjYWFatM9AloG9hMyLB3DOKeZz4XC6xaPpQrKfgsABACgcQXsAMuLVj26gdFK53v6KbqJjTBU6CBkx3iMUvSJBaG/MEY4Vnc2FG5wuZFPU7iT9Q6AAABsWeAe8EgRg+bxrVDGWgdAAAC2FAD9dLuOQulbjmadAyAAAA1JwP4USl9yO+sbAAEA2JEELKFg+or39QVQrG0ABABgRwKQKrxK4fQFP+pNi6xrAAQAoLESoC8L+ooC6nlOZj0DIAAATZWAIRa1roWmcznrGAABAGiuBIy1sFUw7JibdXMi1jAAAgAQiQQcS0H1FA8KSaxdAAQAIFIBiLfoClvYPvqTTQ3rFgABAIi0+P9BeJnC6in05s0ThATWMAACANDUwl8s3Egx9TSvCcNYzwAIAEBjCn+SMEP4hgLqG27jDgAABABge8V/tPAWBdOXfCecpW80ZK0DIAAA9YW/UriHIhkI3hX2Yt0DAsAgAMVfn/NfT2EMHIuEZGIAEACAYH7rv5xCGGie129/iAdAAACCU/zLhGcpgCCsE/YhLgABAPB/8d9d+ILCB1twiZBIjAACAOC/wp8gzBF+odhBAzwplBAvgAAA+Kf4t6aVLzSStcIuxA0gAADeL/7thNUUNmgC+vbHw4gfQAAAvFv8OwkfU9CgGehPRccQR4AAAHiv+O8kfEYhgwg5gXgCBADAO8W/Jzv9IYpMJ64AAQCwv/j3c66DpXBBNDmL+AIEAMDe4j9I+NqvRSg5Lk7lJiWpkrRU1T6zheqek6UGFOSqES0LjDO4ME/1zstWnbIyVHl6mipMSVYtEhJU2N8ScB5xBggAgH3Ff7jwrR8KTUsppv3yc9SEsmI1o2O1WtSrVq0Y1Ec9O3Kg9TwyrL+6tm83dW6XdurQqlI1XGShOiNdxYfDfpGAi4g3QAAA7Cn+fYQNXi0qRVLwR7cuVGd1bqvuGtjbE4W+qTw8tL+6rHtndWB5G9Uhs4WKC3taAmYSd4AAALhf/FsJH3qtiOgieHL7KvW3Ab18WfB3xIND+6mLduqohhXlq6S4OC8eEeT+AEAAAFws/snCU14pHPo7+UEVJeqWnXsEsug3xAND+qnpHapV56wML0mA3mvSiTgEBADAHQFY5oVi0Ss3W83v0Vk9PYJivyNu3bmn2q+klVfeCrwt5BCLgAAAmC3+U20vEDsX5Kqr+uxEYW8G/xjURx1QVqxS4q0XgZVCPDEJCACAmeI/WPjR1qIwpChfXdevG4U8Cqwc3FcdXFGi0uLjbZaAucQlIAAAsS/+Zba2+K1okaYW9qylcMeAewf1qes/YLEETCQ+AQEAiF3xTxNetC35p8rT6bFtK9STIwZQrGPMFT271DUeslAAvhO6E6eAAADERgAW25b4dUe8u//Qm+JsEC1aU2sqbNwo+F8tqcQqIAAA0S3+A5zz11Yke118dJc+CrJ73NivuypNS6VTIAACAD4u/knCa7YkeV10dPGhCNvRYXCUXXsD9ObUWuIWEACA6AjAmbYk+F1aFaqHh/Wn+FqGfhtj0ScB3ZwqjtgFBAAgsuJfI3zvdlLXt9wd366SYmsxV/fZSeUkJdoiAUcTv4AAAEQmAA+6ncwTwmF1Tpd2FFkPcNuAnqpVaooNArBO31NBDAMCANC84n+QDUf8/ty9M8XVYz0Dqluk2yABtxDHgAA0vwBk6wYbwk3CC8Inzuvg94UnhcuFoUICE+q74p8vrHUzgWclJtLK16sXDA3tp3bKzrRBAkYTz77KSwlOzbncqUHvOzXpE6dG3eTUrGwEoPmDnKjv3BZ+aGSQ/U/YlQXqq0C7xs3EnZYQr67tSztfL/PIsP423DD4Lr0BfJOTdnVqTWPm/QenhiUiAE0b5NoIur0tFTJZrJ4PtC5unvnXu8kX9OxCEfUB9w/pa0PnwJOIa0/no0yntjRn7l+09ViobYMc7xz32hhhsOlXMsNYuJ4OuOvdStZx4ZC6sGsHiqeP+PsfeqvClGQ3BWC17mVBbHsyFw1zakok87/RqW3xCICZs9769UsXFrAnA65C+MmtZD29A939/MjN/XuojMQENyXgEOLbk28if4jiGjgTAWj4tf/GKAfcCzZ/f4EG18ICt5K0vn+eYulfFvbsouLCYbcE4N80B/JUHkp0akg018BGmz4H2DTQsbrhbRaL2VNBVyRscCNBd8rK4Da/AHBEdZmbbwHGEOeeyUWzYrQGXrTlwdSWgZ4Z477cPVjQngm6C91IzBkJCerOgb0okAHg6REDVZ+8HLcE4Fni3BN5qIdTO2K1DmYiAL+f8/8hxkG3ikXtiaDLEr5yIzHPYdNfoFg5uK/KT05ySwKGEu/W56JVMV4DP9jQJ8CGgZ5oIOA2eqEpA0EXOtWNhLxfSSuKYgBZ1Ku27n4HF9YcDyT2N5/baGAdTEQAfu2aZCLoxrO4rQ66FKeTltFkXCBPgdzsF1z2adPSrbcAfJa0NxeNN7QGbkIAor/LsiHOYHFbHXSHuJGIOe8fbP45pJ/KTnTl9sDriHtrc9EZhtbACwiAuae++Sxuq4NupekkrDeCUQThzE5t3RAAvdclmdi3MhfNN7QGPkEAzLV7vZHFbW3A5cZ4x+02W/3ePqAnBRDUM0JXdy4N2oP4tzIf3Who/n9BAMwF200sbmsDbrLp5Du5soTiB79xY7/ubmwIvIb4tzIfmdqXphAABICAC4X+YfqWvweG9KPwwWYMKco3LQDruB8AAUAAEACO3BhMvAdV8PQPW6OvfnbhM8Bo8gACgAAgAEENtj+aTLgp8XF1TWAoeLAt+uUb7xB4FXkAAUAAEICgBtvdJhPuBC77ge2wpFetaQH4nAvLEAAEAAEIYqBlCt+bWgOJcWF176A+FDrYLjuZPxEwinyAACAACEDQAm2iyUQ7qDCPAgc75OzO7UwLwJXkAwQAAUAAghZof+XCH7CNR4b1V6nx8SYFYA35AAFAABCAoAXamyav+318+M4UOGgUo1sXmn4L0JqcgAAgAAhAUIIsw2AXyLpLXyhs0Fiu6NnFtADsTl5AABAABCAoQTbQZIJd0rsrhQ0azdMjBqrClGSTAnAmeQEB4C6A2HEri9uqIPuTqSDLTUqq6/dOYYOmMK60tUkBuIO8YE1uupW7AMwN9neGBnsli9uqILvaVHId1bKAggZN5uJuHU0KwHvkhcDdTPodAvBrIwwTg/00i9uqIPuXqeR6RqcaCho0mYeG9ldx4bBJCcglN1iRm54x1QQKAQiFPjQ02G+wuK0JsBST1//eNbAXBQ2aReesDJMCMIz8YEV++reh+f4QAQiF3uGsbeACrJeppFqcmkIhg2YzuaLEpACcQn6wIj+tMTTfbyMAodCrQfneAr/N+eGmkupexRz/A88cB7yR/GBFftpgaL5fRQBCof8zGGBcuuH+fMcJ15ma81M6VFHIoNmsGtLXpAC8JaSSJ1zNT0kG5/v/EIBQ6HGDA57PIndljhP1hSfCYuFjk+f//9KjC4UMIiIjMcGkBHwr3O7ck5FF/jCeqwoMzvVjCEAodL/BAa9ikRub1zRhX+F6YZ3hrmq/cc8felPEwEsbATflB2GFcJhQSF4xkrdqDM7v/QiA2fvgu7PIYzqXYWGsbmpisL9Dg+gLXWgABB68F2Bb/Cw8IkzlM0FMc1hPg3P6dwQgFFpucMCHsMhjNo9DDO/n2CHtM1tQwCBijqwuUzata2G1MFnvpyH3RD2PDTM4j8sQgFDoPIMDvg+LPOrz19HwW5xGM5IOgBAFzq9tr2xc304zrV3JQ1HNZ2MMzt85CEAodJTBAT+YRR61eWvpbOr7ydLkqPYraUUBg4jRG0ltXeMOD/B5M2p5bYrBeTscAQiF9jA44H9ikUc8X+nCTOFry5OiOrC8DQUMIuaqPjvZLgDKuVRNb7gtI09FlN9OMDhnuyEAYq4GB/xSFnlEc9XWYJvMiDmiuowCBhFzc/8eyitrXlgv7Em+anaOW2BwrroiAKFQUZDOXXo4MHYRvvRQIlTHt6ukgEHE6KOkXlr3ztuAM/SpHHJXk/Pcs0HqS2PL0bGNBptsxLPQmzxHJzvHkDyVCE/ryC2AEDkPDu3nNQGoR99rn04Oa1LDsu8Nzc0GK36zJQP/nsGg6MJib9Ktfdd5NPmpWV3aUcAgYp4aMcCrAqB5WSgnn1n3OfodBOD3gX/C4MBPZrE3ak6KDb8Oizont+ceAIiclYP7elkANGvpgWLXJWW6qRMC8PvAX21w4Bew2Hc4Hz2Ejzye9OqucqWAQaRc36+71wVA86MNx84sz3tLDM7HIgTg94E/0eDAP8di3+5cVAuf+iDhqT2LiyhgEDGXdu/kBwGo3xw4njzXYO57weBcHIkAuNN+UV+wkcSCb/AmrLd8kuxU//wcChhEjN5M6peYcPLfYPLdVrkv2eBmdE1fBMCdKxg1PVn027y972kfJTqVlZionh4xgCIGEbF76yI/CYBybufsTN7bLP/1MnyxUzoCsPkEmPzmfBSLfrOxjxfu8lmSq2Nhzy4UMWg2WiCzRSR9GBv/E9qQ/1xpSf8fa363RROwIki3MAW4+5VR9i9tTSGDZrOkV63ya2w4lwllkQPrcuAyg+N+MwKw9QTMMbnwWfS/jfsMHyc4VZiSrJ6hkEEzOaCs2M8CoHmQPVF1efBfBsf8VARg6wk4wPA3mFYs+tBYZ2ewr5PcOTQEgmZw/5C+qkVCgt8FQHN1wPNgmeHx3hUB2Pa98iYn4Tiu8w19HoDkplqlpqjHh+9MUQOe/htmnwDnwmmGx7oQAdj2RKwxOAlPBVwA7ghQcuNiIGgSdw3spRLjwkESAJ17cwOaC5832ZrZqt9u2URcY3jRlwd0wY8PUvHXZCYm1N3qRnGDHaH3jAwqzFNBixHh2gDmwhrDYzwXAWh4MiYanozpAW3281kAk5tqn9lCPTqMTwGwfQ6rKgti8a9n94DlwzMMj+8IBKDhySg0vCnt1QAKwC0BTm5qWFE+pwKgQS7s2kGFAxwfwuqgHA2UvzjhbYNj+52+YRUBsKcfs1U7Mg2M7ZhQsJNbHQeWt0ECYCsW96pVKfFxihgJRp8U+dvb8LiutG4MLJyUCw1PykMBWex5wsckt9/fBPA5ADbt958QDhMbvzMqADnxUcNjehICsONJGeLCYu8dgMW+3Jbk0qE4Sf31uNbq4EFZru8JYGNgsHlqxIC6bpFursNrj26lZo7JV1lpVr19eF9fkOPjfNjThTHtggDseGKShK8NT8xtPi/+7YSf3E4qRVkJaunhLdX65W3VN1e3VS9cWK7CYfdPB+gjgvQJCB7ze3RWbTPSXV1/7Vonqa+deFh9RZU6YXSuio+zRgKm+jgn3mhaqKwcB0sn52rDk/Ozn2/Hkr/r3U4mu3RNV+/Nr6pLdJsyeqd0K5KdbhakOwayN8D/XNevm+qdl23Fups/uWirmFh1Wokqzk2wZUNgsg/zYa0LD0TnIQCNn6ChLiz2h3n6jz76aeaiAwq2SnL1rDy1xLq7A/QrYX2LIFcJ+4dbdu6hjq4pVx2zMqxZa/kZ8WrtlTXbjIsP/lKlRtam8xYgNjnxIRfGsRoBaPwEhYUPXJikCTz9Rw/9en/xoS0bLP719KhIsXIjVFZiouqfn6P2LC5SkytK1Mntq9S5te3VeWA1p3eqUUdWl6n9SlqpwYV5qiw91cr1ddreeduNiy+W1qihndJ4CxD9+09Mj+Hj1o6HxRN1gQsT9aHQgqf/6DBnO0/+m6I3QbHrGoJESmJ4m5/EtuSTRdWqZ2UKbwGikw9Tnc2NpsfvUASg6ZPV3qXFPpen/8iZvmduo4q/5qur2qrKokQKAwSGQ4dmNzo+3hdRaNsqibcAkefD810Yu2+FTASgeRP2jAsTpjcEDuHp30xyq+cf09u4fiIAwMg+k8z4uh3/TYmPf19S6fbGwKkez4dDnNxuetyut3pcLJ+0Y1xa7LphTkue/pvOfn0yfjvm11SmDM6iQIDvuWFq62bFx/MXlKuc9HjeAjSvxfwal8ZtGALQ/InLEda7NHEPCvEeXfBt3Hj6716Ror5cVtOs5KZZs7DaluNPADFhzx4tmh0fmnunt3Hz33+wR/v9r3JpvF7WG9oRgMgmcI6LC/5cjwrAqW4c93t8VllEyU1z2wnFFArwJbrT39uXVUYcI+P6Zbr1Gx72YC4828U5H2v9+Hjk9c13Lk2gvpnwEA8u+n+bHqujR+ZEnNgsSHAAMWPBIUVRiQ8tERmpcW7lw3IP5cGjXZzv1/XbBwQgOhP5Zxcn8mcv9QeQvz6mx6h1ToL6eFF11ARAN0Fplc2nAPAPI7qkRy0+NLMnFLj1W87ySB6cZPhq+S2Z5Ilx8shkFgs/uDiZP+qrIz0yVgtMj8/1x7SKanLTPHNemcpM5WpW8D4d2ySrjxZWRzU+1i2rqfv/14Xf87YHcuBeTs52a87f9sr+MS+91l7sciBrAdnD8jFKFr4wOS66XWm0i389980oUckJXNEK3kW/ydJH+GIVHy79roEW58D9he9dnnfPfDb2kgBUuGx19Z8DTrJ4jPYzOR6pSWH16tyKmAmARh+ZiqM/AHiQFilx6qlzy2IaHy7tl1lqaf47w+XX/pr3hEQEIDYTvNCS4L7axjOx8ne3yXH40645MU1u9Vx6YCEFBTxFQnxY3XVym5jHxuvzKty4Pvgr3VbXsjef11ky92M9VVM9JgDZwieWTPQTNjULck5LGHtDop/KX7u4wogAaKbtkUthAc+w8JCWxmJjt24t3PiNB1iS90qdXGzDvK/w3IkxDx5xm2hRoK8VDrRkXP5k8rfv3r2FsQRXzwXjC9x42gFoNHrPytLDWxqNiztPKg5ksdPf2p23ETbM/QahEgEwM/EPWBb4/xBKXB4To92u7pnWxrgA1Cc73VCFYgM29vh/4IxS4zHx9XJXLtPa4NZnAPlr7eRcm+b/dE/WUo8KQFsLdnpuiW5ZPFVIcGE8UpyANPJb27dOcqX41/PS7HK3b0cD2IwuJcnqjXmVrsWEfjvmwu8eaTjPJQnHmj7p1Aje0P82BCA4LR63x3+FKSZFQP6Gm/yNlx1U5KoA1N8bsEvXdIoPuI7+HPbJompX4+F/V1TVncox/NvnGspv8cJkZ4e9jWtgsGfrqIcFQO/8/I/FieEdfXmGCRGQv4tM/S7dnOeTxdWuC4BG3zp46l55KoleAeACifFhddreeXWv4G2Ih0kDjB8JfMlA4d/fecK2dR0s9vStsR6/8rbWxXsCmnIudKberRrDcXjR1O+ZOirHimS3KS/OLldDO6VRlMAYA9un1V3Pa1McPDKz1I27AYpitLP/nNCvVxDbvA5e0J9fEQB3JeAgjyQN3URopTAumj0EnON/xppfPDGrzDoB2LRpUEkedwhAbDf6md7l3xRc2Aw4MYpvdPcW7nVype1r4Usv7vr3nQBY0ia4OYvnJucTQasIf/sEU//u4twEaxNfPZ8uqVGn7JHLZwGIet+Lw4Zlqw8XVFu9/o8cnm28KVqEu/kPFe4UvvHQetAPXHv6onb6RAC0PT7n4QTzkjBb2EUoDzXhGkn5W27q33no0GzrBaCeVy6qUEeNyObIIETc7lp/W491S99ocfuJxnsCfNjIPJUqdAv9ekvfHOF5C9r2NpfZfqibvhEAZ4GVCZ/7JPHoI33/Em4VztPNhoTeuhPiNn73/0z9u+44qdgzAlDPZ0tq1KJDW6peVSkUNGg0VUVJdVfurr6iynPrPSXR+Nuvjls8jHXVnQKF852n+7c88lq/MTwU8shNf4ESAGfx7WLBhUGx5mPhUeFKJ8CM/HfTU+LU50trPCcAm/LkOWXqkCFZdb+FIgfb6t+/Z48W6u5T2lizs785DO9i/Hisbspzu/Bv4Scfr5E39Z4rX9VMP/0YRwIO8JFtWoNOjF4u/puiRWbVaSXqrDH5aljnNIQgwAW/Z2WKOn7XHHXr8cXWf99vLBcdUMD8Rp933e72igA0XgKmePj7kpXo1+h+EYAt+eqqturRmaXqwvEFao/uLVRtaXLdhkcXXqVCDND3RxRkxtd1sBzUIa1uk6i+qc/t5j2xPBbLvEeV1X7Y8R8YAXAk4GgWbvR2QL83v8q3ArA9dNMjfeXqY2eX1t1DcO3RrdTVR9mH3vBoaj3o/5aNY1DPLfI0r3vyvzynvO4bvpdf5zeX8oJEcld00LfPtvdtnfTrD3Mk4EQWcOTo16RBLP5eQhc+U+tB/7cYc7uZMjiL3BU5elN5ra9rpJ9/nCMBp7OQI+OI4dkkVQQAAfAQlx9cRO6KvPj38n199PsPdCTgGJ/vTo0pi338/R8BQAD8iAttgf2EvmOmOhC1MQg/0pGAkcI6FnfTefa8MpIqAoAAeIi1V9bUbX4kfzWZB4ScwNTFoPxQRwI6OLf0sdCb0AlN75InqSIACIC36FCcRA5rGrq3SmKgamKQfqwjAflOIx0WfCPoU80GQAQAAfAi4/plksMaf1HbyUGrhYEUAEcCkkz20GcDICAAYJoLxtMQqBF8KuwexDoYWAHYRAT2F9YSBGwARAAQAL9xz7Q25LDto9sXFwS6Bgb5xzsSUORcWEFAsAEQAUAAfMMHf6kih22bL4SJQa99CMDmIqBv3PuS4Ni8V/q6ZTUkUwQAAfAohZnx5LKtLy5qTc1DALYlAcXOAiFQhNK8RJIoAoAAeJjuFVyD7aA/9R5CnUMAGiMCewuvBT1o+rdNJYkiAAiAh9G3eAY8j33rXJueSW1DAJoiAfHCwcL7QQ2e8f0zSaIIAALgYUxeEmUZPznn+nndjwBEJALJwgnCZ0ELoml75JJEEQAEwMOcPy6QRwH1pu4O1C8EIJoikCGcHSQR0BeKkEQRAASAdeEBfhRuFvpSrxCAWL8RmCQ86feguvOkYpIoiR4B8DD/PL3E74X/Y+EcXvUjAG7IQFdhkfC1H4Pr+QvKSaIIAALgYd6YV+nXwq8fwA7QXV2pRQiA2yKQKRwtPCv84pcg+3QJPQAQAATAy+g+HnFh3xR9fZHbPKE7dQcBsFUGWgtHCPcK33s12JITwiRQBAAB8AEZqXFeLvovCTOFWuoLAuA1GUgX9hGu9trmwez0eJInAoAA+ICirAQvFfwfnNtaTxQqqCMIgF9kIE7oJhzlCMEbNn8uKM5NIHkiAAiAD6goSLR9E9/f9HW8Qn+9yZp6gQAERQqyhRHCmcI9whpbArOmZRLJEwFAAHxAh+Ikm4r948IC5yRVJXUAAYCtPxt0FvYQjhMudRpbvCysNxWsO5UlkzwRAATAB/Qwex+A/tT5mLBcOM25cl2/+cwgvyMAELkg/J17AAABgMYyoH2qSQHoT55GACB2AvCCiUAe3iWd5IkAIAA+YGRtukkBqCJPIwAQOwH40kQg61vESJ4IAALgffbqafRGwHTyNAIAsRMAI4E8pk8GyRMBQAB8gI5lU2uDHI0AAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAIAAIACAAgAAAAoAAIAAIAAIACAAgAAgAAoAAIACAAAACgAAgAAgAAoAAIACAACAACAACgAAgAAgAAgAIAAKAACAACAACgAAAAoAAIAAIAAKAACAAgAAAAoAAIAAIAAIACAAgAIw5AoAAIACAAAACAAgAAoAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAAAIACAAgAIAAIAAIACAAgAAAAoAAIAAIAAIACAAgAAgAAoAAIACAAAACgAAgAAgAAoAAIACAACAACAACgAAgAAgAAsAgIAAIAAKAACAACAACAAgAAoAAIAAIAAKAAAACAAgAAoAAIAAIACAAgAAw5ggAAoAAAAIACAAgAAgAAgAIACAAgAAAAgAIACAAgAAAAgAIACAAgAAAAgAIACAAgAAAAgAIACAAgAAAAgAIACAAgAAAAgAIACAAgAAAAoAAIACAAAACAAgAAoAAIAAIACAAgAAgAAgAAoAAAAIACAACgAAgAAgAAoAAAAKAACAACAACgAAgAMAgIAAIAAKAACAACAACAAgAAoAAIAAIAAKAAAACAAgAAoAAIADBFgD5SxHaCkOE/YXx4ApGAnk/BAABQAAQgKZDjnaH/Z3arGt0SlQEQP4ShSnC3cIGg4sIXIY3AAgAAoAAgCfZ4NRsXbsTmyUA8tdNeJnBRAAAAUAAEADwJLqGd2u0ADhP/bOEHxk8BAAQAAQAAQBPo2v52dt6G7Bl8S8WXmLAAAFAABAABAB8ha7txdsTgBUMEiAACAACgACAL1mxTQGQv0MYHEAAEAAEAAEAX3PIZgIgfyXCVwwMIAAIAAKAAICv0bW+ZFMBuI9BAQQAAUAAEAAIBPc5tT/UjsEABAABQAAQAAgUuvaHpjMQgAAgAAgAAgCBQtf+0CoGAmgFjAAgAAgABApd+0NvMBDAGwAEAAFAACBQ6NofWs9AAAKAACAACAAEivUIACAACAACgABAQAWATwCAACAACAACAAH8BMAmQGATIAKAACAAEMBNgBwDBN4AIAAIAAIAATwGSCMgQAAQAAQAAYCgNQKiFTAgAAgAAoAAQABbAXMZECAACAACgABAQC8D4jpgQAAQAAQAAYBAsPl1wJtIwAoGBxAABAABQADAl6zYrOZvIQDFwksMEiAACAACgACAr9C1vbhBAXAkIFGYJfzIgCEAgAAgAAgAeBpdy8/WtX2rer/l/8UmItBNeJnBQwAAAUAAEADwJLqGd2uwzjf0/7DJ24Apwt3CBgYTAQAEAAFAAMBqNjg1e8q2nvobLQBbyECK0FYYIuwvjAdXoBUwIABgqwCQo91hf6c26xqd0ui63tj/IdgBbwAAAQBbBYAc7bF6wiAgAAgAAoAAIAAIAAIACAACgAAgAAgAAoAAAAIACAACgAAgAAgAIACAACAACAACgAAAAgAIACAACAACAAgAIACAAAACAAgAIACAAAACAAgAIACAAAACAAgAIACAAAACAAgAIACAAAACAAgAIACAAAACAAgAIACAAAACgAAgAIAAAAIACAACgAAgAAgAIACAACAACAACgAAAAgAIAAKAACAACAACgAAAAoAAIAAIAAKAACAAgAAgAAgAAoAAIAAIAAIACAACgAAgAAgAAoAAAAIACAACgAAgAAgAIACAACAACAACgACAWwLwi4lA3qcXAoAAIAB+QMeyobXxMzkaAYDYCsB3JoJ5ZG06yRMBQAB8gI5lQ2vja3I0AgCxFYAvTARz/7apJE8EAAHwATqWDa2NNeRoBABiKwAfmQjm2tJkkicCgAD4AB3LhtbG2+RoBABiKwD/NRHMFYWJJE8EAAHwAZVFiabWxsvkaAQAYisAr5sI5sLMeJInAoAA+AAdy4bWxpPkaAQAYisAL5gI5rSkMMkTAUAAfEB6cpyptXE/ORoBgNgKwJOmEv365SRPBAAB8DI6hsPhkKm1cSc5GgGA2ArASlOJfs3CapIoAoAAeJiPF1UbWxfCDeRoBABiKwDXmAroty6rJIkiAAiAh3lbYtigACwmRyMAEFsBmG0qoF+cXU4SRQAQAA/zksSwQQG4hByNAEBsBeB4UwH92NmlJFEEAAHwMI/PKjMpAOeSoxEAiK0AjDMV0H89rjVJFAFAADzMrccXmxSAGeRoBABiKwB/MBXQ540rIIkiAAiAh5lzQIFJATiWHI0AQGwFoK2pgJ48KIskigAgAB7m8GHZJgVgCjkaAYDYCkCGqYAe2D6NJIoAIAAeZniXdJMCsD85GgGA2EvAWhMB3TongSSKACAAHkbf6WFQAIaQnxEAiL0APGIioHUHsU+X1JBIEQAEwIOsW1ajEuLDJgWgmPyMAEDsBeAKU0H99LllJFMEAAHwIP+aU2Gy+H9DbkYAwIwAHG0qsG+YylFABAAB8CJ3nGT0COCL5GYEAHx2FHDW2HySKQKAAHiQeZMKTQrATeRmBADMCECeqcA+cGAmyRQBQAA8yNEjc0wKwDnkZgQAzEnAGhOB3a9tKskUAUAAPMguXY0eAZxEXkYAwJwA/NNEYGemxtXdKU5CRQAQAG9Rlm/0CGBv8jICAOYE4DJTwf3kOZwEQAAQAC/x5qVGrwHWZJOXEQAwJwCHmQruuRMLSaoIAALAemiIT8jJCACYFYB+pgJ8r54tSKokfATAQxxm9g6Ax8jJCACYFYBMUwFekBlPUkUAEAAP0alNskkBWEZORgDAvAR8YCrIX5xdTmJFABAAD/DhgmoVFzb6/X86+RgBAPMC8A9TQT5/chHJFQFAADzAbScUm94AuDf5GAEA8wIw11SQj+9PQyAEAAHwAifulmtaADqSjxEAiF2hTxGqhEHCAcI04c/C86aCvCSPq4ERAATAC/StSTUtAPpI8lRhT2En3amUvI0AQNOKfLw2aWGCMEe4R3hJWGs4mBvk9XkVJFgEAAGwmLVX1qikhLAN+eJb4Q1hlbBU7xMQRgi55HsEIOjFPkMY4FjzlcJzwgZbCn1DXHl4S5IsAoAAWMzKU0uU7XlEeFe4TZjhSAFvDBAAXxf8ON0qUzhTeFz40QNBuhX79s4gySIACIDFTN8zV3kxtzhSsEwYo484UzcQAK8X/dbCZH1Npk2v8SMhLSmsPllcTaJFABhzS6lumaR8kGs2Cg8JpwidqScIgFeKfpGzaF/2Q8HfFsuPJPkjAKwBG9F3dvg077wvLNKboKkzCIBtRT/B2f16p1df7TeF3brRFhgBQABs5OTdc5Xf84/wprOhsCX1BwFws/C3Fy4S1gQg6H5D7zD+aCGfARAAxtw2KgoSVYBykX7YukPYTZ+goiYhAKYK/2DhwSAV/S1ZdCinARAAxtwmHp1ZqgKck1YLM4UcahQCEMvC/3CQC389w7ukk3QRALCI43fNUeSm0DrhbCGLmoUAUPhjREJ8WL0/v4rEiwCAJZTmJZKbfudL58g1xwkRgGYX/p0p/A1z2UFcDoQAgA08dGYpOWnbfC6cKrSgpiEAjS382cIS4RcCqGEGtE8l+SIAYAFTR/H6fwd8LIylviEAOyr+Y4O2q7+56PvGuRsAAQB3Wb+8rSrOTSAnNQ59VLs1tQ4B2LLwtxH+ToA0jWN3ySEJIwDgIn89rjW5qOkbBQ8XwtS+gAuAXgTOhTzrCYymk5Eap9bQEwABANfo3zaVXNQ89P6uGgQguMU/U7iLQIiMC8YXkIgRAODsvxfRN7AeigAEr/i3c+6qJggipCQvQa1bVkNCRgDAMPv3zSAHRYe/6JbuCEAwiv8ewlcsegoCAsB8e5X/XFJZ14+D/BM1dHfXPATA39/7z+J4X/TpXpFCUkYAwCAnjM4l90Sfd4VaBMB/xT/NOQLCIo8RK08tITEjAGCATxZXq6y0OPJObPhG2BcB8Ffxf4CFzTXBCAAC4AfmTSok58QW/Zb4SASA4g+NJBwOqZdml5OgEQCIceOfyiL6/huSgCMQAIo/NJKDB2WRpBEAoPGPnyTgMASA4g+NID4upJ45r4xEjQBAjJ7+O7VJJteYl4BDEQCKPzSCoZ3SSNYIAMSAK6YUkWPck4BDEABvHPX7W0A7WumrLz9wGhw9LzwqrHBuwjL677nthGISNgIAUd753zLbtUt/HhKedXLLaqePys8BlIAJCIDdAnC6T4u7Dr6bhUuEk4RxwgChQkjewZgcbvrf3LZVEt0BEQCIIqftnedW/lm1nYetdKFY6CeMF2YIC50Hj9eF73yWi/Xv6Y4A2Fn8d/WBleozqI8LlwsHC10ibVGpBcGNK44vnlRI4kYAIAq88+dKlZ7s2rn/YRHknngnhx3stNt9ygdS8L5QgADYVfyrhC89uJi0sDwjnC30EeJiND4zTP+23BbxavUVVSRwBAAiZPKgLLfy03MxyEUJQldhmvCYRx/aHvTL3QF+KP76NdS/PLR4vhauFyYK+YbGKMuN+w+mjsohgSMAEAHPnV9ed7rGpVy1n4HclC/8UbjVY9eyX4YA2CEAN3tkwTzvfI9v4dI4zTH9mxPjw+rlOTQHQgCguYysTXcrX70ZqzeS28lRKc7ngmc8ktMPRADcLf5TPPBNf4nQ04KxaiV8b3oMdu9Oi2AEAJrDPdPauJm7DnM5X/UQllm+Z0D/2zoiAO4skCLhC0sXhi6082y7XtKREePjceOxrUnoCAA0gS+X1bjZ9OejHZ0uMpizcoRzhG8tzfVPmn5TggD8ujBusXRT33Kh1NIxq3Fj001eRrz67+VsCEQAoLFM28PV636nWZi79BvMpcJPFub9qQiA2cWwp4WL4B9eeB3kbLbhtkAEAAGwlIfOLHVz4986IdPi/NVZuNfCjd0lCICZBZDpdKOyafIP9dD4dXe6WhkfqwWHFJHgEQDYDp8tqVHVLZPczGfneySPTXBkxZY6cA8CYGbiF1g06foca6UHx/B6N8arRUqcen1eBYkeAYAGOHJ4tpv57FObn/63kcfKhScsqgcTEIDYTnhft55et2CjMN2rmz/06yq3dtcObJ+mvl5OokcAYEvund5GhcOu5rWjPJjLdLfBWZbsDfjUto3ffhOAf1ryyn+UD/onnO/WGM6eUEDCRwBgE9YsrFYleQlu5rXXdDH1cD4b4eRmt+vDxQhAbCZ4gAWTq2/W6+GT9skZbtwUqElJDKvnL6BBEAIA9UwakOl2bhvtg5zWU/jM5XHUxxULEQD/Pf2/6cXv/bbdFFhP94oUbgxEAEC46bjWbhf/+32U09oK7/EWwEcCYMHT/yt+ugFqi+9nr7g1rocNy6YAIACB5s1LK1VBZrzbvUtqfZbXip1PGrwF8IkAuPn0v8bWxj5RGttRbsrVZX/k2mAEILhH/vSbMJcfbpb6NK+1cToa8hbAywLg8tP/tzb08Tcwxve5NcYJ8eG63c8UBAQgaIztm2HDXSWtfJzXerjYQtgTbwG8MImrXHw1to/fi/8m3bVcO0aTkx6v/jWH/gAIQHA4e798G86tnxmA3Lavi0fH5yIAkU1elYuTNy0Ixd/N64I3pV3rJPXRwmqKAwLge245vljFhV0v/m/YcuGPgdw23aUx/kpIQwCaP3HnujRxDwnhgAmAvov7P24mpRFd0tVXV1EgEAD/8tz55XUdMS24tKxfwPKbW585D0IAmjdhccIHLjX6KQ9ScGyx38LVTotTR+VQKBAAX/K/K6pURUGiDa/+LwlgbitxnshNj/UjCEDzOzu5ERyHB7H4bzLul7udoLg0CAHwG7rnxaAOaTYU/7dsfy0dw9x2mEtjXoMANH2ybnRhou4LcvF3xj1deNfNJJWUEFZ3nlRM4UAAfIPueWFB8ddv9wYFPL/d78K4X4AANG2SsoUNhifpB6Es6ALgjP9wt5OVbhd89ykcD0QAvM8pe+TaclvdFeS2UJkLF6F9aOs9C7ZO0lEuBMd8iv9mc3Cl2wkrNYkeAQiAtzlt7zxbir9uj9uC3FaX2y51Yfx3RwAaP0GPG54cbYQtCY7N5iBLWO124koTCVgxAwlAALzHmfvm21L8NSPIa7/ltpYuvAW4DQFo3OTkuNCUZi6Bsc252N2G5JWeHKdWnlpCUUEAPMOssVYV/6Xks61y2zwXTpclIgA7npixhidmvZBPUDQ4H5dZIQEpcer+05AABMB+zh9XYFPx15fipJPLtsprRS60CR6MAOx4YpYZnpSLCIjtzkei8KQNyUw3UHngjFKKDAJgLXMOsKr4617/HchjDea2SwzPx2wEYMeTYvq7c3uCoVE3a31mQ1LLSI1TD52JBCAA9jFvUqFNxV9zAPlru3mtneH5eBEB2P6EdDE8IU8RCE1qzPSzLW8Cbj+RPgEIgD1cZNeTv2YBeatRec3k281fbNtsbttknELXP6uD5SxbElx8XEjNnVhI8UEAXEXfXXHk8Gzbiv9zQhI5y8rugH9EABqejAcMH/3LIgiafD/DfTYlu8OHZXOBEALgCh8vqlajatNtK/5fBPUukwiOO5s8EngjArDtiUh1uvGZmogbCIBmzVOeS5c0bfcWwTUBv0oYATDLG/MqVac2ybYV/19sbThjeU67weAcfWbTTbM2TUJvw8EyhsXf7LnqY1jWdkhHScavz6tAABCAmPPwWaWqKCvBtuKvuZD81Kx8tofheapGANz9FqMbDWWz+D15s1aDFGbGB/aEAAJghuuPaVXXotrC4n+frf3mPZDLMoSNBudqLAKw9SRcwe5/zwXOebYlQn2J0DUBLFAIQOyZOSZfhcMhG4v/8/T5jziXPRbE2wGD2v//HBZ91ObtGtsSok7SR4/MUWuvrEEAEICI+WRxtZrQP9PGwq+cq7u5x8RbJ5xWIACbD37YaclragIGsOij2inwfhuTo94X8PS5ZQgAAtBsHplZqqqKkmwt/p/rZjbkoajksf4G520NArD54FcZ7v2fwKKP6vxlCi/ZmCSTE8LqwvEF6uvlCAAC0LTz/fo2v4T4sK3Ff4MuWuSfqOWwBOErg/PXEgH4ffDHGBz4+1nwMZnD1rYdD9yUwR3T1JuXViIACMAOeXVuhepbk2pr4VdOR859yDtRz2ErDM7hrgjA7wN/jsGBn8dij9k8dhK+tDVx5qTH1+3iRgAQgIZYcljLuvsmLC7+mqnkm5jkr9kG5/A0BOD3gb/d4MAfxGKP6VwOEr63OYFOHJBZ18UNAUAA6ll9RZXat3eG7YVfM4c8E7PcNdHgPP4VAfh94J8xOPDdWOwxn899DZ+rbTLlBYnqthOKEQAEQN19ShtVnJvgheK/1KYucj7MW7UG5/IJBOD3gf/Q0KD/KCSz2I3M6V62dQtsqI3wCxeWIwABFADdOXKfXp546tcspPjHPGclGXxweR8B+H33palrZl9joRud291s/xyg0Tu9dd+ADxdUIwABEIBPl9SoGXvm2drRb1vMJ58Yy1mvGHwYjUMAQqE2BgPpJha58fkd5RxZsj7R5mXEq8sOKvLk7YIIQOO46shWXnndX88l5BGj+epGg3PbCgEIhfoaHPCLWOSuzPEw4VuvJN3OJclqxYw2CICPBODRmaWqT3Wqlwo/G/7cyVUXG5zfXghAKLSfwQE/nkXu6umAb7yUgPfq2UK9MrcCAfCwALzz50o1aUCmrT38t8e55A1X8tQZBud4bwQgFPqTwQHfn0Xu6lwPMNzyOWLi40Jq/74Z1rcURgA257+XV6lpe+SqFilxXiv8mrPIF67lqGOC1M/BhgGfyx0AgQqwvjY3C9oeo2rT1arTShAAiwXgpdnlasrgrLpbIb24xoQZ5InA9AKYjQCEQjcYHPBKFrkVQdZeeNujCbruW/Itxxdbdb9A0AXgwTNL1Z49Wqi4cMirhV+flplEfnA9N402OOfXIQCh0J0GBzyFRW5NoOULj3pVAjQdipPUlYe3VOuW1SAALqAFTItY/7apysvrSPhM2Jm8ELhbAe9AAEKhlYYG+0sWuJWNN67xePJWpXmJ6vxxBeqtyyoRAAN8vrRGLTikSLVrneT1wq95nTeTVuWkDgbn/j4EwNxT4PsscGuD7nThF68nc/36eUinNLX40JbG7xrwuwCsX/5ry94DB2aqzNQ4PxR+zSohixxgVS5qZXD+H0UAQqHnDA32f1jgVgfeWOE7nyT2ui5z+/XJULceX2zkE4FfBeCRmaXqmJE5qigrwS9Ff9PWvgnEvnV5KMXgGngOAQiFXjU02C+xwK0Pvt7CGp8l+roOg4cPy67bqIYA7JgXZ5erU/fKU5VFiX4r+sppe04/ErvzkKnOpa8iAKHQO4YG+2kWtyeCr0R4wYeJv46KwkR1xPBsdeOxreuuoEUAfn29//isMnXeuALVrTxF+XXuhXX6fgzi3PocZOoh5B0EIBT6yNBgP8Ti9kwAJguX+7gQ/LZnoHtFijpxt1z191PaqM+W1ARCAOoL/oXjC9TondJVVlqc8vtcC08J5cS3J/LP64bWxEcIgLmmMCtY3J4LxD2FtQEoDnUkJYTVgPap6ox989QDZ5Q0ae+AzQIQ0IJfj97cOpvv/Z7KO08G5WSaDYNt6rrYO1jcngxGfVvkIwEqGJttJOxalqzG9ctUZ43Jr/ts8MKF5dsUA1sEQLfgvW9Gibr84CJ17C45amRt4Ar+puhXySOIY8/lnHtNNX9CALgKGHa8RuKFmcJPAS0kW70p0E2I9u6VUbdZThdkLQim/vuLDm2pnjmvTF1/zK//3Qn9M1XPyhQ/Hc+LyhlvoZD49WS+ucnUOkEAEABo/Fr5g/A/igtYzEbhFCFMzCIACAACANFdL7nCXRQasBB9oqk3cYoAIAAIAMR23UwSPqXogAXoT1OXCC2ITQQAAUAAwNzbgKV+aCMMnkV3Mu1GPCIACAACAO6soYEGz+4CaNYLxwlxxCACgAAgAOD+zYJnGGzjCcHlb0IxcYcAIAAIANgVvNXCPylSEAM+0M2piDMEAAFAAMD+TYIfUbQgCvwgzGOTHwKAACAA4J1AThWmCV9QxKCZN/ddQw9/BAABQADAuwGdJZwvfENRg0aie010Jn4QAAQAAQB/BHaRMN95pUuRg23xsNCPeEEAEAAzg73R0GBfy+IGZ82VO692f6bggcMLwijiA3StMNU2GgEIhd43NNgXs7hhi7XXSbgdEQg0rwnj6N0Pm+SFiw2tvfcRAHN3L5/M4oYG1mCV8GenuQtF0f/ozpErhJGsf9hGPjjZ0Dp8EgEIhS43NNhDWdzQiM2CJwrvUiR9ybfCQqE96x22kweGGlqPlyMAZgZbHwNLYHFDI9dkvDBGeJyi6Qv0FdIz9N0RrG9oRPwnGDo6PBQB+HWwY33H+yIWNjRzffYUbjC4WRWix9PCeOQfmhH3iwxIaULgBcAZ7F1jONAfCjksaohwjRYKxwpPUVitb9d7kdCVdQsRxHuOUztitU53teJ3WjTgS/080OCr5FDpXDzEDYR2oF/XLhYGsZsfPPBgutSa32jRYGfG4EjgEhYyxHjd7iTMNfAZCzbnO+FmfTmPvgmStQgxiu8l0T76p2sdArDtwR4WxS5t/+byDjC4dsPOE+gi5zU0RTr66FbOdwt/FDJYd2Agrls4tSRal0oNs+r3WTjgXZyuXBE9+VP8weV13N7ZM6AL1tcU72ZfxvOsc4/DIJ70wUUJiPRNgK5pXaz7bZYOeKIwS/ixGRv++OYPNq7ngcK5zs50Og82zHvClcJYju2BhXsCmrox8EenliVa+ZssH/AewqpGHMH6wnn1ym5/8MoO4zHOxURPBPiWwp+djZT6mOUxQlvWB3ggdhc1ok/ARqd29bD693hk0LOd87xnOEnzGqdf88lOIyHO+YKXk0qc88lggnOE7Z/CWp8V+++F/3NepR4l9BXSmH/wcLOgoU4NutipSfOdGqVrVbYnfgeTCWBtkikV9nJeId7ifD740PJPCF8KLwv3CJcJBwm1SDoAAgAA0dlToK80/oMw0Wlzu8DZcKiL7yfOZ4VoisIG4XPhLeEB4WrhHOEwfY2u0JGNtwAIAADYIwvJznfLYqFGd8hzXr/r15e7O5vt9FuGEcIAobvzOUK/fcjXr+lprgOAAAAAAAACAAAAAAgAAAAAIAAAAACAAAAAAAACAAAAAAgAAAAAIAAAAACAAAAAAAACAAAAAAgAAAAAIAAAAACAAAAAAAACAAAAAAgAAAAAAgAAAAAIAAAAACAAAAAAgAAAAAAAAgAAAAAIAAAAACAAAAAAgAAAAACAy/w/MzVpoyGPMW0AAAAASUVORK5CYII=";

    public MenuButton() {
        super(getTextureRegionDrawable("icon/menu.png"),
                getTextureRegionDrawable("icon/menu.png"),
                getTextureRegionDrawable("icon/menu.png"));
        setSize(VIEWPORT_HEIGHT * 0.1f, VIEWPORT_HEIGHT * 0.1f);
        setPosition(VIEWPORT_WIDTH - 5 - getWidth(), VIEWPORT_HEIGHT - 5 - getHeight());

        preferences = Gdx.app.getPreferences("AncientTerror.xml");
        ButtonUtils.addClickListener(this, () -> {
            openMenu();
        });
    }

    private void openMenu() {
        if (!menuButtonClickable) {
            return;
        }
        getColor().a = 0f;
        menuButtonClickable = false;
        MapStage.darkenWorld(0.85f);

        Dialog dialog = new Dialog(get("menu.title"), skin);

        ImageTextButton restartGameButton = createNiceButton("icon/restart.png", get("menu.restartGame"));
        ImageTextButton musicOnButton = createNiceButton("icon/volume_on.png", get("menu.musicOn"));
        ImageTextButton musicOffButton = createNiceButton("icon/volume_off.png", get("menu.musicOff"));
        ImageTextButton vibrationOnButton = createNiceButton("icon/vibration_on.png", get("menu.vibrationOn"));
        ImageTextButton vibrationOffButton = createNiceButton("icon/vibration_off.png", get("menu.vibrationOff"));
        ImageTextButton strobeOnButton = createNiceButton("icon/migraine_on.png", get("menu.strobeOn"));
        ImageTextButton strobeOffButton = createNiceButton("icon/migraine_off.png", get("menu.strobeOff"));
        ImageTextButton saveAndExitButton = createNiceButton("icon/save.png", get("menu.saveAndExit"));

        byte[] bugButtonData = Base64Coder.decode(BUG_BUTTON_DATA);
        Pixmap pixmap = new Pixmap(bugButtonData , 0, bugButtonData .length);
        ImageTextButton reportBugButton = createNiceButton(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))), get("menu.reportBug"));

        TextButton closeButton = createNiceButton(get("menu.return"));

        if (restartButtonDisplayed) {
            dialog.getContentTable().add(restartGameButton).size(280, 50);
            dialog.getContentTable().row();
        }
        Cell<ImageTextButton> musicToggleButtonCell;
        if (NewMusicBox.getInstance().isEnabled()) {
            musicToggleButtonCell = dialog.getContentTable().add(musicOnButton);
        } else {
            musicToggleButtonCell = dialog.getContentTable().add(musicOffButton);
        }
        musicToggleButtonCell.size(280, 50);
        dialog.getContentTable().row();

        Cell<ImageTextButton> vibrationToggleButtonCell;
        if (preferences.getBoolean("vibration_disabled", false)) {
            vibrationToggleButtonCell = dialog.getContentTable().add(vibrationOffButton);
        } else {
            vibrationToggleButtonCell = dialog.getContentTable().add(vibrationOnButton);
        }
        vibrationToggleButtonCell.size(280, 50);
        dialog.getContentTable().row();

        Cell<ImageTextButton> strobeToggleButtonCell;
        if (preferences.getBoolean("strobe_disabled", false)) {
            strobeToggleButtonCell = dialog.getContentTable().add(strobeOffButton);
        } else {
            strobeToggleButtonCell = dialog.getContentTable().add(strobeOnButton);
        }
        strobeToggleButtonCell.size(280, 50);
        dialog.getContentTable().row();

        dialog.getContentTable().add(reportBugButton).size(280,50);
        dialog.getContentTable().row();

        dialog.getContentTable().add(saveAndExitButton).size(280,50);
        dialog.getContentTable().row();
        dialog.getContentTable().add(closeButton).padTop(30).size(280,50);
        dialog.getContentTable().row();

        dialog.pack();

        dialog.setPosition(VIEWPORT_WIDTH/2f - dialog.getWidth()/2,
                VIEWPORT_HEIGHT/2f - dialog.getHeight()/2f);
        dialog.show(InfoStage.getStageSafe());


        ButtonUtils.addClickListener(musicOnButton, () -> {
            musicToggleButtonCell.getActor().remove();
            musicToggleButtonCell.setActor(musicOffButton).size(280,50);
            NewMusicBox.getInstance().setEnabled(false);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("music_disabled", true);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(musicOffButton, () -> {
            musicToggleButtonCell.getActor().remove();
            musicToggleButtonCell.setActor(musicOnButton).size(280,50);
            NewMusicBox.getInstance().setEnabled(true);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("music_disabled", false);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(vibrationOnButton, () -> {
            vibrationToggleButtonCell.getActor().remove();
            vibrationToggleButtonCell.setActor(vibrationOffButton).size(280,50);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("vibration_disabled", true);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(vibrationOffButton, () -> {
            vibrationToggleButtonCell.getActor().remove();
            vibrationToggleButtonCell.setActor(vibrationOnButton).size(280,50);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("vibration_disabled", false);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(strobeOnButton, () -> {
            strobeToggleButtonCell.getActor().remove();
            strobeToggleButtonCell.setActor(strobeOffButton).size(280,50);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("strobe_disabled", true);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(strobeOffButton, () -> {
            strobeToggleButtonCell.getActor().remove();
            strobeToggleButtonCell.setActor(strobeOnButton).size(280,50);
            Gdx.app.postRunnable(() -> {
                preferences.putBoolean("strobe_disabled", false);
                preferences.flush();
            });
        });

        ButtonUtils.addClickListener(restartGameButton, () -> {
            if (restartGameAction == null) {
                return;
            }
            dialog.hide(Actions.run(() -> {
                MapStage.brightenWorld();
                menuButtonClickable = true;
                getColor().a = 1f;
                restartGameAction.restartGame();
            }));
        });

        ButtonUtils.addClickListener(closeButton, () -> {
            dialog.hide(Actions.run(() -> {
                MapStage.brightenWorld();
                menuButtonClickable = true;
                getColor().a = 1f;
            }));
        });

        ButtonUtils.addClickListener(reportBugButton, () -> {
            dialog.hide(Actions.run(() -> {
                new ReportBugDialog(MenuButton.this, skin).show(InfoStage.getStageSafe());
            }));
        });

        ButtonUtils.addClickListener(saveAndExitButton, () -> {
            dialog.hide(Actions.run(() -> {
                MapStage.brightenWorld();
                menuButtonClickable = true;
                getColor().a = 1f;
                Gdx.app.exit();
            }));
        });
    }

    public void setRestartGameAction(RestartGameAction restartGameAction) {
        this.restartGameAction = restartGameAction;
    }

    public void displayRestartButton() {
        this.restartButtonDisplayed = true;
    }

    public void setSkinProperty(Skin skin) {
        this.skin = skin;
    }

    private ImageTextButton createNiceButton(String icon, String text) {
        return createNiceButton(CustomAssetManager.getTextureRegionDrawable(icon), text);
    }

    private ImageTextButton createNiceButton(TextureRegionDrawable textureRegionDrawable, String text) {
        TextButton.TextButtonStyle textButtonStyle = new TextButton("", skin).getStyle();
        ImageTextButton.ImageTextButtonStyle imageTextButtonStyle = new ImageTextButton.ImageTextButtonStyle(textButtonStyle);
        imageTextButtonStyle.imageUp = textureRegionDrawable;
        ImageTextButton niceButton = new ImageTextButton(text, imageTextButtonStyle);
        niceButton.getLabel().setFontScale(0.5f);
        niceButton.setSize(280, 50);
        niceButton.getLabel().setStyle(new Label.LabelStyle(CustomAssetManager.getBitmapFont(FONT_ADLER), Color.WHITE));
        niceButton.getImage().setAlign(Align.left);
        niceButton.getLabel().setAlignment(Align.left);
        niceButton.getLabelCell().growX();
        niceButton.getImageCell().width(50);
        return niceButton;
    }

    private TextButton createNiceButton(String text) {
        TextButton niceButton = new TextButton(text, skin);
        niceButton.getLabel().setFontScale(0.5f);
        niceButton.setSize(280, 50);
        niceButton.getLabel().setStyle(new Label.LabelStyle(CustomAssetManager.getBitmapFont(FONT_ADLER), Color.WHITE));
        return niceButton;
    }
}
