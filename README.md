### AdvancedRanking Plugin
![logo](https://cdn.modrinth.com/data/cached_images/298be919e678d1ca8c3fbe27633f7c89cbda06a1.png)
**Overview:**
AdvancedRanking is a comprehensive Minecraft plugin that provides a detailed player ranking system based on configurable ratings. Players can rate each other, and the ratings are stored in a database for persistence. The plugin includes a GUI for easy interaction and supports PlaceholderAPI for dynamic placeholders.

**Features:**

- **Player Ratings:**
  - Players can increase or decrease each other’s ratings using a GUI.
  - Ratings are stored in a SQLite database for persistence.
  - Configurable cooldown period to prevent rating abuse.

- **Graphical User Interface (GUI):**
  - Interactive GUI for players to manage and view ratings.
  - Customizable using resource packs for unique visuals.
  
![Without resourcepack](https://cdn.modrinth.com/data/cached_images/fddfa07ace6af5f3d300b9758e898666753f38cc.png)

![With resourcepack](https://cdn.modrinth.com/data/cached_images/91f1b6a01a688735be361f1bf18d35910f2f2fe1.png)

- **Commands:**
  - `/ranking`: Displays the player's ranking.
  - `/profile`: Shows the player's profile with their rating.
  - `/setrank`: Allows setting a player's rank directly (admin command).

- **PlaceholderAPI Integration:**
  - Provides placeholders for player ratings and other related data.

- **Logging:**
  - Logs plugin activities and errors for debugging and auditing purposes.

**Installation:**
1. Download the AdvancedRanking plugin.
2. Place the downloaded file into your server's `plugins` directory.
3. Start or restart your server to load the plugin.
4. Configure the plugin as needed in the generated `config.yml` file.

**Configuration:**
- The `config.yml` file allows server administrators to configure settings such as database connection, cooldown periods, and GUI options.

**Usage:**
- Players can use the provided commands to interact with the ranking system.
- The GUI can be accessed to view and change player ratings.
- Admins can manage player rankings and configure plugin settings as required.

**Commands:**
- `/ranking`: Opens the ranking menu.
- `/profile`: Displays the profile of a player.
- `/setrank <player> <rank>`: Sets the rank of a specified player (admin command).
- `/cancelcleanup`: Cancels the upcoming item cleanup if the player has the necessary permission (`advancedclear.cancel`).

**Placeholders:**
- `%advancedranking_rating%`: Displays the player's rating.
- `%advancedranking_last_change%`: Displays the last time a player's rating was changed.

**Support:**
- For any issues or suggestions, please visit the plugin's support page or repository.

**Metrics:**
- This plugin uses bStats to collect anonymous usage statistics to help improve the plugin.

**Example Config (`config.yml`):**
```yaml
# AdvancedRanking configuration
cooldown_minutes: 1440
use_resourcepack: false

buttons:
  increase: "Increase Rating"
  decrease: "Decrease Rating"

messages:
  en:
    rank_message: "Your current rank is: "
    change_success: "You have successfully changed the rating."
  ru:
    rank_message: "Ваш текущий ранг: "
    change_success: "Вы успешно изменили рейтинг."
```

### Class Breakdown

#### `AdvancedRanking` Class
This is the main plugin class responsible for:
- Enabling and disabling the plugin.
- Managing database connections and operations.
- Copying necessary resources.
- Registering commands and event listeners.

#### `RankingCommand` Class
Handles the `/ranking` command which opens the ranking menu for the player.

#### `ProfileCommand` Class
Handles the `/profile` command which displays the player's profile with their current rating.

#### `SetRankCommand` Class
Handles the `/setrank` command allowing administrators to set a player's rank directly.

#### `RankingGUI` Class
Handles the GUI operations for the ranking system, including creating and displaying the inventory with player heads and buttons for increasing/decreasing ratings.

#### `RankingPlaceholder` Class
Integrates with PlaceholderAPI to provide placeholders related to the ranking system.

**Note:**
Ensure you have the required dependencies like `PlaceholderAPI` and `bStats` in your server's plugin folder for full functionality.
