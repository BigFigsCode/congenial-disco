/*
  EquipmentDao.java
  Purpose: all database operations for the `equipment` table live here.
  Design: no frameworks, just plain JDBC so it’s easy to read in a demo.
  Safety: every method uses try-with-resources so connections are closed.
  Scope: CRUD only (Create, Read, Update, Delete). No business logic here.
*/

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDao {

    // Our project uses a local SQLite file. Keep the URL consistent.
    private static final String URL = "jdbc:sqlite:fitness.db";

    // Helper to open a connection. Short and predictable.
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /*
      CREATE
      Adds a new equipment row. Assumes schema:

        CREATE TABLE IF NOT EXISTS equipment (
          id    INTEGER PRIMARY KEY AUTOINCREMENT,
          name  TEXT NOT NULL,
          type  TEXT,
          notes TEXT
        );

      I only set the three user fields here. SQLite will generate the id.
    */
    public void insert(Equipment e) {
        String sql = "INSERT INTO equipment(name, type, notes) VALUES (?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {

            // Basic null/blank defense so we don’t store garbage.
            if (e.getName() == null || e.getName().isBlank()) {
                throw new IllegalArgumentException("Equipment name is required.");
            }

            ps.setString(1, e.getName());
            ps.setString(2, e.getType());
            ps.setString(3, e.getNotes());
            ps.executeUpdate();

        } catch (SQLException ex) {
            // For class demo, print the stack trace so we can see the exact SQL.
            ex.printStackTrace();
        }
    }

    /*
      READ (all)
      Pulls back a simple, sorted list so it looks good in the UI table.
    */
    public List<Equipment> findAll() {
        List<Equipment> out = new ArrayList<>();
        String sql = "SELECT id, name, type, notes FROM equipment ORDER BY name";

        try (Connection c = connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                out.add(new Equipment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("notes")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return out;
    }

    /*
      READ (one)
      Handy for editing a single item by id.
    */
    public Equipment findById(int id) {
        String sql = "SELECT id, name, type, notes FROM equipment WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Equipment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getString("notes"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // not found is a valid outcome
    }

    /*
      UPDATE
      Standard update by id. I keep the SQL in column order to match the model.
    */
    public void update(Equipment e) {
        String sql = "UPDATE equipment SET name = ?, type = ?, notes = ? WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {

            if (e.getId() <= 0) {
                throw new IllegalArgumentException("Valid id required for update.");
            }
            if (e.getName() == null || e.getName().isBlank()) {
                throw new IllegalArgumentException("Equipment name is required.");
            }

            ps.setString(1, e.getName());
            ps.setString(2, e.getType());
            ps.setString(3, e.getNotes());
            ps.setInt(4, e.getId());
            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /*
      DELETE
      Straightforward delete by primary key.
      If there are future foreign keys referencing equipment.id,
      SQLite will enforce the rule we set (CASCADE/RESTRICT) at schema level.
    */
    public void delete(int id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /*
      Quick smoke test (optional). Drop in a temporary main() if you want
      to verify DAO wiring without touching the GUI.

      public static void main(String[] args) {
          EquipmentDao dao = new EquipmentDao();
          dao.insert(new Equipment("Dumbbell", "Free Weight", "5–50 lb set"));
          System.out.println(dao.findAll());
      }
    */
}
