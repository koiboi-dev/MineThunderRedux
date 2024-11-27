package me.kotos.minethunder;

import me.kotos.minethunder.damagemodel.datatypes.ShellData;
import me.kotos.minethunder.updaters.Updater;
import me.kotos.minethunder.utils.VectorUtils;
import me.kotos.minethunder.vehicles.GroundVehicle;
import me.kotos.minethunder.vehicles.Vehicle;
import me.kotos.minethunder.vehicles.settings.ShellSettings;
import me.kotos.minethunder.vehicles.settings.TurretSettings;
import me.kotos.minethunder.vehicles.settings.VehicleSettings;
import me.kotos.minethunder.weaponry.projectiles.Shell;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args[0]){
            case "tank":
                if (args.length == 1) {
                    new GroundVehicle(((Player) sender).getLocation(), "Challenger");
                } else {
                    new GroundVehicle(((Player) sender).getLocation(), args[1]);
                }
                break;
            case "reload":
                VehicleSettings.LoadVehicles();
                TurretSettings.LoadTurrets();
                ShellSettings.LoadShells();
                for (Vehicle vehicle : Updater.getAllVehicles()) {
                    vehicle.updateSettings();
                }
                break;
            case "shell":
                System.out.println("FIRING SHELL");
                new Shell(
                        ((Player) sender).getEyeLocation(),
                        new ShellData("balls", "hmm", 4, 120, 0.2f, 200, false));
                break;
        }
        return false;
    }
}
