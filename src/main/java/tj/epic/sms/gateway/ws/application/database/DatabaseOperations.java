package tj.epic.sms.gateway.ws.application.database;

import java.sql.*;

public class DatabaseOperations {
	public static long insertNewSMS(String sender, String receiver, String smsBody, int priority, String scheduledTime, String gateway) throws SQLException {
		String query = "INSERT INTO `sms_list` (`sender`, `receiver`, `sms_body`, `priority`, `scheduled_time`, `gateway`, `sms_id`, `status`)"
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (
				Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		) {
			statement.setString(1, sender);
			statement.setString(2, receiver);
			statement.setString(3, smsBody);
			statement.setInt(4, priority);
			if (scheduledTime != null) {
				statement.setString(5, scheduledTime);
			} else {
				statement.setNull(5, Types.NULL);
			}
			statement.setString(6, gateway);
			statement.setNull(7, Types.NULL);
			statement.setString(8, "pending");

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Could not insert SMS");
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getLong(1);
				}
				else {
					throw new SQLException("No ID obtained");
				}
			}
		}
	}

	public static void updateSMSStatus(long id, String status, String smsId) throws SQLException {
		if (id == -1) {
			return;
		}
		String query = "UPDATE `sms_list` SET `status` = ?, `sms_id` = ? WHERE `sms_list`.`id` = ?";

		try (
				Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		) {
			statement.setString(1, status);
			if (status.equals("sent")) {
				statement.setString(2, smsId);
			} else {
				statement.setNull(2, Types.NULL);
			}
			statement.setLong(3, id);

			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Could not update SMS item");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	private static Connection getConnection() throws SQLException {
		try {
			Class.forName ("org.mariadb.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("No suitable driver found for JDBC");
		}
		return DriverManager.getConnection("jdbc:mariadb://localhost:3306/smpp_gateway?user=root");
	}
}
