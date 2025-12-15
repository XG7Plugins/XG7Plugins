package com.xg7plugins.menus.config;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.boot.Plugin;
import com.xg7plugins.config.editor.impl.FormEditor;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7geyserforms.builder.FormBuilder;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigIndexForm extends SimpleForm {

    private final FormEditor  formEditor;

    public ConfigIndexForm(FormEditor editor) {
        super(
                "config-form-index",
                "lang:[config-form-editor.title]",
                XG7Plugins.getInstance(),
                Arrays.asList(Pair.of("path", editor.getCurrentSection().getPath().isEmpty() ? "root" : editor.getCurrentSection().getPath()),
                        Pair.of("saved", editor.isSaved() + ""))
        );
        this.formEditor = editor;
    }

    @Override
    public String content(Player player) {
        return "lang:[config-form-editor.content]";
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = new ArrayList<>();

        ConfigSection section = formEditor.getCurrentSection();

        for (String key : section.getKeys(false)) {
            if (key.equals("config-version")) continue;
            buttons.add(ButtonComponent.of(ChatColor.RESET + key));
        }

        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.add").getText(), FormImage.of(FormImage.Type.URL, "data:image/png;base64,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")));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.save").getText(), FormImage.of(FormImage.Type.URL, "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxMREhIPDhIQFREXEhIYFRUQFRkTGBUYGB0WFhUVFhUYHSggGB0lGxYXITEhJSkrLi4uFx8zODMsNygtLisBCgoKDQ0OGw8QFisfHx4rLC83Ny4vMC03MjcrNzcwLTctMys3Ny4uKzc4Ky0tLTI3Kys1KzUtLS4rLS8tLTAyLf/AABEIAOEA4QMBEQACEQEDEQH/xAAcAAEAAgMBAQEAAAAAAAAAAAAAAgcBBAYFAwj/xABPEAABAgIDBw4LBgQGAwEAAAABAAIDBBESMQUHFyFhkZMGEyIzQVFSVHGBsbLR0hQVFjI1U2Ryc5LiI2ODocLTJDSzwUJiguHw8SWEokP/xAAbAQEAAgMBAQAAAAAAAAAAAAAABAYBAgUDB//EADURAQABAQQHBwQBBAMBAAAAAAABAgMENHEFFTJTgaGxERQzUnKR0RIhwfFBEzFR8CRh4SP/2gAMAwEAAhEDEQA/ALnbDINc2dqDMTZ+bub6BWxVN2xAh7Dzt3eQYMMk19ymlBmI6vib+aDLIgYKrrciCLIZYazrMiBEbXxt5MaCRiAipu2IMQ9h527vZEGDDJNcWW5kEojq+JttuNAY8MFV1uRBFsMtNY2ZECIK+Nu5voJa4KKm7RRzoMQxU87d3kGHQy41hZ2IJPfXxNttxoEN9TE623Egi2GWmsbO1BmIK/m7m+gzrgoqbtFHOgjDFTG7d3kB8MuNYWZUEnvDxVbblQIbqmJ1tuJArhBERC41DRRZlxIEV2t4wRRQSS7co3UHAXcvoS8JxErDdHeLXk1IdP8AlNBLujKg8d99uK62Vhc0Rw/ssdp2M4XY1FHgsGj33didp2IsvtRRZKwueI4/2We0QiX2YxNJloPzuQZdfdjnEZeBRkc5AZfcjDEJeBzucUERfajU0+DQKffcgy++3GdbLwOZ7ggyL7scCgS8Cj3nIMMvtRhjEvA53uKA++1GOMy0Dme5Bl192ORQZeBRkc5AZfcjCyXgc73FBgX2I1NbwaDT77kEn32o7rZaBzOcEBt9uOBQJaBR7zkGGX2IwxiWg87nFBh99qMfOloFOR7ggkb7scijweBRkc5BJl9uK2yVhc8Rx/sg+spfYJiNMaWaGU7Iw3kkZQ0jHyUoLNkZtkyxsSG5robgHNcywhB9XRCzYiijKgk6HU2QtyoMMbXxu5MSDOtjKgOIoobRWyW5UFc33bsvhQocm0kGLS553ajcQbyOcf8A5yoOFlJRrAMQrbpVyudxs7vRH27av5n/AH+FNvl+tLxXP37Kf4j/AH+WxQp3ZCF2lCdkHa+cx5jvdd0LwvMR/Rryno97t41Hqjq7W8gPs5un1kLquVGXhZtQbwzIFQbwzIFQbwzIFQbwzIFQbwzIFQbwzIMFo3hmQRIG8MyCJA3ggpC+36Rd8CD+pBxqAgILvvaD/wAbL8sb+pERhqX2B/Afjwuh6Curm7WzkPSVcdGR/wAWjj1VDSeKr4dIfSYgB4qu5jujKF73m62d4o+iuP8Ax4Xa82l3r+uif/W1qS1TxbmxNbiVnSzjS5o3P87Mu+N1U+83a0u9f0V/tb7tebO8UfXR+l23PnIUWGyKxzXtcKWuGOkGxR0hOGCDs6aMuNBmJSTsLP8ALixoMUHKglrdXZ05aOVBUN+KJWnJf4A67ltRtRnDWvZnKXjlX5QRZBB85nzHe67oXhevBr9M9HvdvGo9UdXaXj9rm/iQuq5UVeVnICAgICAgiSggSgiSgpG+36RPwIP6kHGoCAgu+9p6Nl+WN/UiIw1b7P8AIfjwuh6Cubm7UzkPSVcdGYWjj1U/SeKr4dIbS6CA+UxADxVdzHdB3wo15u1neKPor/SRdrzaXev66P22NSWqWJc2LrcWl0s40uaMdH3kPLvjdVPvN2tLvX9Ff7XC7XmzvFH10fpdspOsmWNfCcCxwrNc00ghR0h9q2t4rd3eQNcyIItcSaD5v/KMaCpL8jQJyXq+oHXctqNqM4a17M5S8cq/KAwjIg+cz5j/AHXdBXhefBr9M9HtdvGo9UdXaXj9rm/iQuq5UZelnICAgICCJKCJKCBKCJKCk77PpA/Ag/qQcagICC772no2X5Y39SIjDVvs/wAh+PC6HoK6ubtTOQ9JVx0ZhaOPVT9J4qvh0hsqegCD5zEuHiq7mO6DvhR7zdrO8UfRX+nvdrzaXev66P29i9ddWNBmzJB1MJ+uEi2q5oprN3qaKCFS7azmytJon+J7F1sbWLWzptI/mO1ccIAil9uXFiXm9CqEAxKwqCmmzHZiQU/fghls5Lg0bQLPfctqNqM4a17M5S8oq/KAwgIPnM+Y/wB13QV4Xnwa8p6Pe7eNR6o6uzvH7XN/EhdVyoy9LOQEBAQRJQRJQRJQQJQRJRhSl9j0gfgQf1Iy45AQEF33tPRsvyxv6kRGGrfZ/kPx4XQ9BXdzNqZyHpKuOjMLRx6qdpTFV8OkNlT0AQEGze8dRdZhP33UcqTfcRXnK7XHDUZQut7a+NvJjUVLNbORBJzABXFvagp+/A8unJcn1A67ltRtRnDWvZnKXlFX1QBAQfKZ8x/uu6CvG8+DXlPR73bxqPVHV2d4/a5v4kLquVGXpZyAgIMEoIEoIkoIkoIEowiSgpW+v6QPwIP6kZcegICC772no2X5Y39SIjDVvs/yH48Loegru5m1M5D0lXDRmFo49VO0niq+HSG0p6AICDZveNpuswH77qOVKvuIrzldrjhrPKF1RHVMTbLcaipZXKDDWEGsfNQVJfkeDOS9HqG9dy2o2ozhrXszlLySr6+fsICD5zPmP913QV4Xnwa8p6Pe6+NR6o6uyvH7XN/EhdVyo69rOQEGCUESUECUESUESUYQJQRJQV/q51Dx5yZExLvggGG1rmxS5pBbTjBDTSCDkRlzuC+d4cp87/20DBfO8OU+d/7aBgvneHKaR/7aCy9SlyTJysKWe4OcwOrFtlLnOeQKcdArUcyMPDvs/wAh+PC6HoK8uZtTOQ9JVw0ZhaOPVTtKYuvh0hsqe54gIPvqAaTdVgFv23UKpV9xFecrvccNZ5Qu6E6qKH251FSyuEERErbDmp5EFRX4odWclx9w3ruW1G1GcNa9mcpeUVfXz8QEHzmRsH+67oK8Lz4NeU9Hvdp/+1Hqjq7G8efs5sfeQuhyo69rOQYJQRJQRJQQJQRJQQJRhElBhAQEBAQEHGX2T/Af+xC6HoK9uYPsmch6SrhozC0ceqm6UxdfDpDaU9AEBB973z6LrMPxuoVSr7iK85Xe44azyhdgbXxnFuYlFSzW0GXUUbGitktyoKevv0+GS9anaW2++5bUbUZw1r2Zyl5pV8fPhGRAoSY7fsRPZ92nc2bmrnxTGknGhwoIorgi0Nc3do3CMecqqXvRlrZVTNnH1U/9f3hbbnpWytaYi0qimr/v+0vZwk3T4MHQO7yh92t93V7Snd6sN5T7wxhJunwYOgd3k7tb7ur2k71Ybyn3gwkXT4MHQO7yd2t93V7Sd6u+8p94Ywj3T4MHQO7yd2t93V7Sd6u+8p94MI10uDB0Du8ndrfd1e0nervvKfeGMIt0uBB0Du8ndrfd1e0nervvKfeGMIl0uBB0Du8ndrfd1e0nervvKfeDCHdLgQtA7vJ3a33dXtJ3q77yn3gwh3S4ELQO7yd2t93V7Sx3q77yn3gwh3S4ELQO7yd2t93V7Sd6u+8p94MId0uBC0Du8ndrfd1e0nervvKfeDCHdLgQtA7vJ3a33dXtJ3q77yn3gwh3S4ELQO7yd2t93V7Sd6u+8p94MId0uBC0Du8ndrfd1e0nervvKfeHl3WurOXQLBNOoY00hobUaDYSG2l1GKkqRd9HW9tV2TTNMf5n7I950nd7GntiqKp/xH392zDYGgNFgFAVss7Omzpiin+0KhaWlVpXNdX95+6S3aCAg+97yjxsymij7a33HKl33EV5yu9ww1nlC6otNOws/wAu/wAyipbGPKglrdXZ89HKgqG/FErTkufuG9dy2o2ozhrXszlLzCr4+fMICAgICAgICAgICAgICAgICAgICAgICD7Xv2U3WYPjdRypd9xFecrxcMNZ5QuwO1vEce6oqWa4gi1xJqnzf+bqCpL8jQJyXo9Q3ruW1G1GcNa9mcpeWVe3z4WQQEBAQEBAQEBAQEBAQEBAQEBAQEBAQfTUASLrMIt+26hVLvuIrzXi4Yazyhd0JocKX25lFSyqEB0QEVBbZmQU/fghls5Lg+pb13LajajOGtezOUvOKvb56LIICAgICAgICAgICAgICAgICAgICAgIPre9dRdZhP33UcqXfcRXnK83DDWeULqe2vjbyY1FSzWygkYdUVxbbnQU/fgiVpyXJ9Q3ruW1G1GcNa9mcpecVe3z0QRe8AEuNAFpK1rrpopmqqeyIbUUVV1RTTHbMtbxlC4YzHsUPWd18/KU7Vd83fOPk8ZQuGMx7E1ndfPyk1XfN3zj5PGULhjMexNZ3Xz8pNV3zd84+TxlC4YzHsTWd18/KTVd83fOPk8ZQuGMx7E1ndfPyk1XfN3zj5PGULhjMexNZ3Xz8pNV3zd84+TxlC4YzHsTWd18/KTVd83fOPk8ZQuGMx7E1ndfPyk1XfN3zj5PGULhjMexNZ3Xz8pNV3zd84+TxlC4YzHsTWd18/KTVd83fOPk8ZQuGMx7E1ndfPyk1XfN3zj5PGULhjMexNZ3Xz8pNV3zd84+TxlC4YzHsTWd18/KTVd83fOPk8ZQuGMx7E1ndfPyk1XfN3zj5PGULhjMexNZ3Xz8pNV3zd84+TxlC4YzHsTWd18/KTVd83fOPk8ZQuGMx7E1ndfPyk1XfN3zj5PGULhjMexNZ3Xz8pNV3zd84+WxCihwrNII3wpVlbUWtP1UT2wh2tjaWVX0Vx2SmvR5iD63vmU3WYPjdRypd9xFecrzcMNZ5QupzqmIcuNRks1xBhoNNLqauWzIgqS/GR4ZL1aNobZ77ltRtRnDWvZnKXmlXt88EGpdban8g6QoGk8LXw6ujorF0cekukvfah5WelPCJjXq+uvbsH1RQ2ijFRlVRXR0uCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oGCuQ9o0n0oPI1XXvJOVk48xB1/XGMpbWfSKaQMYoyoOHuFtZ989AVn0N4E+qfwqenMTHpjrL0V1nGEE9QIPjZtFv21nuFUu+4ivOV6uGGs8oXbCIA2duXHiUZLKRkQY1ytsKMlPJkQVDfhh1ZyXFNP2Deu5bUbUZw1r2Zyl55V6fPBZGpdbaX8g6QoGksLXw6uhorF0cekrJvO+j/x4v6VUV1dwgICAgICAgICAgICAgICAg5u+L6Nm/hjrNQU5cHaz756ArPobwJ9U/hU9OYmPTHWXorrOMIJ3v30XWafjdRypd9xFecr1cMNZ+mF11dcx2bm+oyWa3lQZcBRSKK2S3Kgp+++SZyXrU7S2333LajajOGtezOUtEq9PnjCDUuttL+QdIUHSWFr4dXQ0Vi6OPSVkXnvR/48X9KqK6uvuhEe2FFdBbWiiG8saf8AE4Alo5zQg/P0bVhdCsa83MNdSaRTUoO6KtAq8lCCPlhPcdmPnQPLCe47MfOgeWE9x2Y+dA8sJ7jsx86B5YT3HZj50DywnuOzHzoHlhPcdmPnQPLCe47MfOgeWE9x2Y+dA8sJ7jsx86B5YT3HZj50DywnuOzHzoA1YT/HJj50F66kpqNFk5eLNgiO6GC+kVScZquLf8JLaCRlQad8T0bN/DHWagpy4O1H3z0BWbQ3gTnP4VPTmJj0x1l6K6zjCD6XvgPG7KbPtuo5Uy+4ivOV6uGFs/TC6YhIOwsyY8ajJZWOVAEOqa5sty40FR34olaclyPUNt99y2o2ozhrXszlLzyr0+diDTuttL+QdIUHSWFr4dXR0Vi6OPSVj3n/AEf+PF/Sqiurt6UES0boGZAqDeGYIFQbwzBAqDeGYIFQbwzBAqDeGYIFQbwzBAqDeGYIFQbwzBAqDeGYIFQbwzBAqDeGYIFQbwzBAqDeGZBKlBzl8P0bN/DHWagp64O1H3z0BWbQ3gTnP4VLTmJj0x1l6S6zjCCWoFlN1mgffdQqmX3EV5r3cMLZ+mF1sdUxHlxKMls65yoINeSahs7EFSX42ATkuB6hvXctqNqM4a17M5S0Cry+dCMtO6+0v5B0hQdJYavh1dDRWLo49JWLeg9H/jxf0qpLs6+dmhChxIz6arGPeaMZoaC40ZcSCqX33I9Jqy0ADcBc8kDKRRTmQYwtzHFpfO/tQMLcxxaXzv7UDC3McWl87+1AwtzHFpfO/tQMLcxxaXzv7UDC3McWl87+1AwtzHFpfO/tQMLcxxaXzv7UDC3McWl87+1AwtzHFpfO/tQMLcxxaXzv7UDC3McWl87+1AwtzHFpf5n9qCzdT11hNy0Kaa0tERtNU46pBLXCndFIONB518L0bN/DHWagqC4G1H3z0BWbQ/gTnP4VLTuJj0x1l6S6rjCBqCdRdZpH33UKpt8xFecr3cMLZ+mF2w218brbMSjJZUCA54Iqi1BT995hE5L0+pb13LajajOGtezOUtMq8vnTCDUuvtL+QdIUHSWGr4dXR0Ti6OPSVh3ofR/48X9KqS7O1cKcRAINoOOnIg5Z97y5xJPg1FJsbEiNA5AHUAZEEcHVzeLu00XvoGDq5vF3aaL30DB1c3i7tNF76Bg6ubxd2mi99AwdXN4u7TRe+gYOrm8XdpovfQMHVzeLu00XvoGDq5vF3aaL30DB1c3i7tNF76Bg6ubxd2mi99AwdXN4u7TRe+gYOrm8XdpovfQZF7q5vF3aaL30HTSsuyExsKE1rGNaGta0UBoFgAQeFfC9HTfwx1moKhuBtR989AVm0P4E5z+FS07iY9MdZekuq4ogle/dRddpP3/UcqbfMRXnK96Pwtn6YXTFbXNLbLN5RkxioUEjDq7Pdto5UFQ34Ilaclz9y3ruW1G1GcNa9mcpaRV5fOhBp3X2l/IOkKDpLDVcOro6JxlHHpKwr0fo/wDHi/pVSXZ2cSIGgucQGgEknEABjJJQcc++dIAkB0cjfbCNByikg/kgxhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkPaNF9SBhPkN+Y0X+6DrZGcZGhsjQXB0N7Q5rhug8tnIg8W+D6Om/hjrNQVHqf2o++egKzaH8Cc5/Co6dxMemOsvSXVcYQZ1AMpuu0fG6jlTb5iK8170fhbP0wupzqmIY93GoyYxriDDaadlTVy2ZEFS346PDJerRtDbPfctqNqM4a17M5S0Srw+dMLI07sbS/kHSFB0lhquHV0dE4yjj0lYN6P0f8Ajxf0qpLs6+bgNisfCfjY9jmuAxYnAg4+QoK0fekNJqzmx3K0Gk0ZSHgE8yCOCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIGCR3HG6A/uIAvSO443QH9xBYlw7mMlIEKWhlxbDbRS61xJJc40b5JKDzb4B/8dN/DHWagqTU/tR989DVZdD+BOc/hUdO4mPTHWXpLrOMIGoOnxs2imn7az3CqbfMRXmvej8LZ+mF2QqKNnRT/m3lGTDFkQY1ytsOankQVDfgh1ZyXH3Leu5bUbUZw0tNicpahV4fOmEGndjaX8g6QoOksNVw6ujonGUceku/vSfyH48X9Kqa7u0RhrPujBaS10aCCLQYjQRyglGUfGcD18DSs7UYPGcD18DSs7UDxnA9fA0rO1A8ZwPXwNKztQPGcD18DSs7UDxnA9fA0rO1A8ZwPXwNKztQPGcD18DSs7UDxnA9fA0rO1A8ZwPXwNKztQPGcD18DSs7UDxnA9fA0rO1AF04Hr4GkZ2oy2kYc/fA9HTfwx1moyqbU/tR989DVZdD+BOc/hUdO4mPTHWXprquKIGoF9F12n43UcqdfMRXmvmj8LZ+mF1FuuY7NxRkw1tBlzQBWHnf83EFP33nEzkvT6lvXctqNqM4aWmxOUtUq8PnIjLTuxtL+QdIUHSWGq4dXR0TjKOPSXe3pf5D8eL+lVNd3WT0Jz4URjHVHuhva13BcQQHcxNKMKPfqAugCR4NTQbWxIZBygl1KMseQV0OKn54XfQPIK6HFT88LvoHkFdDip+eF30DyCuhxU/PC76B5BXQ4qfnhd9A8grocVPzwu+geQV0OKn54XfQPIK6HFT88LvoHkFdDip+eF30DyCuhxU/PC76B5BXQ4qfnhd9A8grocVPzwu+geQN0OKn54XfQXHqWkYkvKQIEdwdEZDAcQaQMZIaDuhoIbTkRhqav/R038MdZqCqNT21H3z0NVl0P4E5z+FS09iY9MdZemuq4ogagADddoNn2/UcqdfMRXmvmj8LZ+mF0RSWmhlmTGoyYVigNYQa5s7UFR343h05L0eob13LajajOGlpsTlLUKu75yLI0rsbTE5B0hQdJYarh1dHROMo49Jd5em/kPx4v6VU13dkgICAgICAgICAgICAgICAg8DV96Om/hjrNQVTqe2o++7oarLofwJzn8Kjp7Ex6Y6y9RdVxRBDUG2m67QPvuoVTr549ecr7o/C2fphdkN1TE623EoyYVwgiIhcahssy4kFYX6LllrpaaZSW0OhuO8aazKeXZZkiez7sTETHZLw5aOIjQ9th/I7oKulhbU21EV0/wAvnt4sK7C0mzrj7x/va+i9ni0rs7TE5B0hQdJYarh1dHROMo49Jd3en/kPx4v6VU13dkgICAgICAgICAgICAgICAg8DV96Pm/hjrNQVXqe2o++7oarLofwJzn8Kjp7Ex6Y6y9NdVxUYjw0FzjQBaStK66aKZqqnsiG9FFVdUUUx2zLavVyros9FnANhDa/GeFExNby1axVMtrT+paVV/5l9Au9l/SsqbPyxELgY2vjdyYl5vZnWwgOIIoHnZLcqDVnJGHGhvgTTQ6G8UEO6QdwjEaUFVXXvcTku5z5B+uwjZsgx4G4HB2xdy/kvSztrSzntoqmHjbXeyto7LSmJzea3U1dc2QYmeF2r37/AHneTyRtV3PdRz+XzjalrquBY+BEINorQv7Fa13u3tKfpqr7YnJvZXC7WVcV0URExm3LnXMu3Ks1qXhx4cOsTVGskUm046TuKMmNsDVCbPCM0DsQYadUBs8IzQOxAd5QC3wjNA7EGaNUNv8AEUckDsQYb5QGzwjNA7EAnVBZ/EU8kDsQHDVCLfCM0DsQZaNUJs8IzQOxBgHVAcQ8IzQOxAd5QC3wjNA7EGaNUNv8RRyQOxBhvlAbPCM0DsQCdUFn8RmgdiDLhqhFvhGaB2IMNGqE2eEZoHYgA6oLP4inkgdiD4T8pd2LDdBjtjuhvFDmkQRSMRtGPeQaEvqSuqxtDJeIG2+dCz43L3sr1bWUfTRV2Qi21yu9tV9VpR2zxfRupq6xsgxM8LtXp3+87yeTy1Xc91HP5bche/n5hwE44QYYOOu4Pd/pYw0E8pC8rW8Wtrt1TL3sbrYWPh0RC1NT9xoclCbAhNoYKSS7GXONrnb5XikPQigk7CzJixoFBQZ1urs6ctHLlQNsyUc9qBW//Pmp/wBkDa8tPNYga3Ts6ctH+6BW1zFZRj30DXKmxop/JA1upsqafyQKuuY7NzfQNcp2FGSnkyIG15aeaz/tA1unZ05aOTKgV9c2Nm7v/wDLUDXKmxop/JA1upsqacllqBRrmOyjnQNc/wAFGSn8qaECjW8dtPMga3W2dNGTk/6QK9fY2bu+gV6mxt3d5A1urs6acnL/ANoFGuZKOdA1z/BRkp/KmhAq63jtp5kDW6+ypoyWoGuV9jRR+aBW1vFbu7yBrmRBOL5nMEEJPd5kEW7ZzlBKc3Of+yCbfM/0lB8pS08iCM15x5kH3mvNzIMSlh5UHxZ5/wDqKD6Tm5z/ANkH0heZzFB8ZS3m7EGJrzuZB95jzTzIISdh5UHyHn/6kH1nNxBKB5mf+6D4ynncyBN283ag+8bzOYf2QQk93mQfI+f/AKgg+s5YOVBOW80c6D4SvncyDM3aOTtQRQf/2Q==")));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.back").getText(), FormImage.of(FormImage.Type.URL, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ7Ri82Zr0dlnbmnQahDNo1LbIPkKuwlCpH1Q&s")));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.delete").getText(), FormImage.of(FormImage.Type.URL, "https://cdn-icons-png.flaticon.com/512/3687/3687412.png")));
        buttons.add(ButtonComponent.of(Text.fromLang(player, plugin, "config-form-editor.close").getText(), FormImage.of(FormImage.Type.URL, "https://cdn-icons-png.flaticon.com/512/9068/9068678.png")));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {

        List<ButtonComponent> buttons = form.buttons();

        int buttonID = result.clickedButtonId();

        int addID = buttons.size() - 5;
        int saveID = buttons.size() - 4;
        int backID = buttons.size() - 3;
        int deleteID = buttons.size() - 2;
        int closeID = buttons.size() - 1;

        if (buttonID == addID) {
            formEditor.sendAddRequest();
            return;
        }
        if (buttonID == backID) {
            formEditor.sendPage(formEditor.getCurrentSection().parent());
            return;
        }
        if (buttonID == saveID) {
            formEditor.save();
            formEditor.sendPage(formEditor.getCurrentSection());
            return;
        }
        if (buttonID == closeID) {
            if (formEditor.isSaved()) return;
            FormBuilder.modal("config-close-confirmation", plugin)
                    .title("lang:[config-form-editor.close-confirmation.title]")
                    .content("lang:[config-form-editor.close-confirmation.sure-to-close]")
                    .button1("lang:[config-form-editor.close-confirmation.confirm]")
                    .button2("lang:[config-form-editor.close-confirmation.back]")
                    .onClose((f, p) -> formEditor.sendPage(formEditor.getCurrentSection()))
                    .onError((f, e, p) -> formEditor.sendPage(formEditor.getCurrentSection()))
                    .onFinish((f, r, p) -> {
                        if (!r.clickedFirst()) {
                            formEditor.sendPage(formEditor.getCurrentSection());
                            return;
                        }
                        try {
                            formEditor.getCurrentSection().getFile().reload();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .build()
                    .send(player);
            return;
        }
        if (buttonID == deleteID) {

            FormBuilder.simple("config-delete-list", plugin)
                    .title("lang:[config-form-editor.delete-form.title]")
                    .content("lang:[config-form-editor.delete-form.content]")
                    .components(p -> {
                        
                        List<ButtonComponent> btns = new ArrayList<>();
                        
                        for (String key : formEditor.getCurrentSection().getKeys(false)) {
                            if (key.equals("config-version")) continue;
                            btns.add(ButtonComponent.of(ChatColor.RESET + key));
                        }
                        return btns;
                    })
                    .addBuilderPlaceholders(Collections.singletonList(Pair.of("path", formEditor.getCurrentSection().getPath())))
                    .onClose((f, p) -> formEditor.sendPage(formEditor.getCurrentSection()))
                    .onError((f, e, p) -> formEditor.sendPage(formEditor.getCurrentSection()))
                    .onFinish((f, r, p) -> {

                        String path = ChatColor.stripColor(r.clickedButton().text());

                        FormBuilder.modal("config-delete-confirmation", plugin)
                                        .title("lang:[config-form-editor.delete-confirmation.title]")
                                        .content("lang:[config-form-editor.delete-confirmation.sure-to-delete]")
                                        .button1("lang:[config-form-editor.delete-confirmation.confirm]")
                                        .button2("lang:[config-form-editor.delete-confirmation.back]")
                                        .onClose((fo, pl) -> formEditor.sendPage(formEditor.getCurrentSection()))
                                        .onError((fo, e, pl) -> formEditor.sendPage(formEditor.getCurrentSection()))
                                        .onFinish((fo, re, pl) -> {
                                            if (re.clickedFirst()) formEditor.getCurrentSection().parent().remove(path);
                                            formEditor.sendPage(formEditor.getCurrentSection());
                                        })
                                        .build()
                                        .send(player);
                            }
                    ).build().send(player);


            return;
        }

        String buttonLabel = ChatColor.stripColor(buttons.get(buttonID).text());

        formEditor.sendEditRequest(buttonLabel, formEditor.getCurrentSection().getType(buttonLabel));

    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {

    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {

    }
}
