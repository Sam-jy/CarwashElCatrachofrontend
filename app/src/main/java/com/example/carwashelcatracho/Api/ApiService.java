package com.example.carwashelcatracho.Api;

import com.example.carwashelcatracho.Models.ApiResponse;
import com.example.carwashelcatracho.Models.LoginResponse;
import com.example.carwashelcatracho.Models.Quotation;
import com.example.carwashelcatracho.Models.Service;
import com.example.carwashelcatracho.Models.User;
import com.example.carwashelcatracho.Models.Vehicle;
import com.example.carwashelcatracho.Models.Notification;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // =============================================
    // AUTHENTICATION ENDPOINTS
    // =============================================
    
    @POST("api/cliente/register")
    Call<ApiResponse<Map<String, Object>>> registerClient(@Body Map<String, String> userData);
    
    @POST("api/cliente/login")
    Call<ApiResponse<LoginResponse>> loginClient(@Body Map<String, String> credentials);
    
    @POST("api/admin/login")
    Call<ApiResponse<LoginResponse>> loginAdmin(@Body Map<String, String> credentials);
    
    @POST("api/cliente/verificar-email")
    Call<ApiResponse<Void>> verifyEmail(@Body Map<String, String> verificationData);
    
    @POST("api/cliente/reenviar-codigo")
    Call<ApiResponse<Void>> resendCode(@Body Map<String, String> emailData);
    
    // =============================================
    // CLIENT PROFILE ENDPOINTS
    // =============================================
    
    @GET("api/cliente/perfil")
    Call<ApiResponse<User>> getClientProfile(@Header("Authorization") String token);
    
    @PUT("api/cliente/perfil")
    Call<ApiResponse<User>> updateClientProfile(@Header("Authorization") String token, @Body Map<String, Object> profileData);
    
    @POST("api/cliente/cambiar-password")
    Call<ApiResponse<Void>> changePassword(@Header("Authorization") String token, @Body Map<String, String> passwordData);
    
    // =============================================
    // VEHICLE ENDPOINTS
    // =============================================
    
    @GET("api/cliente/vehiculos")
    Call<ApiResponse<List<Vehicle>>> getVehicles(@Header("Authorization") String token);
    
    @POST("api/cliente/vehiculos")
    Call<ApiResponse<Vehicle>> createVehicle(@Header("Authorization") String token, @Body Map<String, Object> vehicleData);
    
    @PUT("api/cliente/vehiculos/{id}")
    Call<ApiResponse<Vehicle>> updateVehicle(@Header("Authorization") String token, @Path("id") int vehicleId, @Body Map<String, Object> vehicleData);
    
    @POST("api/cliente/vehiculos/{id}/eliminar")
    Call<ApiResponse<Void>> deleteVehicle(@Header("Authorization") String token, @Path("id") int vehicleId);
    
    // =============================================
    // SERVICE ENDPOINTS
    // =============================================
    
    @GET("api/cliente/servicios")
    Call<ApiResponse<List<Service>>> getServices(@Header("Authorization") String token);
    
    @GET("api/cliente/servicios")
    Call<ApiResponse<List<Service>>> getServicesByLocation(@Header("Authorization") String token, @Query("ubicacion") String ubicacion);
    
    // =============================================
    // QUOTATION ENDPOINTS
    // =============================================
    
    @POST("api/cliente/cotizaciones")
    Call<ApiResponse<Quotation>> createQuotation(@Header("Authorization") String token, @Body Map<String, Object> quotationData);
    
    @GET("api/cliente/cotizaciones")
    Call<ApiResponse<List<Quotation>>> getClientQuotations(@Header("Authorization") String token, @Query("estado") String estado);
    
    @POST("api/cliente/cotizaciones/{id}/aceptar")
    Call<ApiResponse<Void>> acceptQuotation(@Header("Authorization") String token, @Path("id") int quotationId);
    
    @POST("api/cliente/cotizaciones/{id}/rechazar")
    Call<ApiResponse<Void>> rejectQuotation(@Header("Authorization") String token, @Path("id") int quotationId);
    
    // =============================================
    // NOTIFICATION ENDPOINTS
    // =============================================
    
    @GET("api/cliente/notificaciones")
    Call<ApiResponse<List<Notification>>> getClientNotifications(@Header("Authorization") String token);
    
    @POST("api/cliente/notificaciones/{id}/leer")
    Call<ApiResponse<Void>> markNotificationAsRead(@Header("Authorization") String token, @Path("id") int notificationId);
    
    @POST("api/cliente/notificaciones/leer-todas")
    Call<ApiResponse<Map<String, Object>>> markAllNotificationsAsRead(@Header("Authorization") String token);
    
    // =============================================
    // HISTORY ENDPOINTS
    // =============================================
    
    @GET("api/cliente/historial")
    Call<ApiResponse<List<Quotation>>> getClientHistory(@Header("Authorization") String token);
    
    @GET("api/cliente/historial/aceite/{vehiculoId}")
    Call<ApiResponse<List<Map<String, Object>>>> getOilChangeHistory(@Header("Authorization") String token, @Path("vehiculoId") int vehicleId);
    
    @GET("api/cliente/historial/lavados/{vehiculoId}")
    Call<ApiResponse<List<Map<String, Object>>>> getWashHistory(@Header("Authorization") String token, @Path("vehiculoId") int vehicleId);
    
    @POST("api/cliente/historial/{id}/calificar")
    Call<ApiResponse<Void>> rateService(@Header("Authorization") String token, @Path("id") int historyId, @Body Map<String, Object> ratingData);
    
    // =============================================
    // ADMIN ENDPOINTS
    // =============================================
    
    @GET("api/admin/dashboard")
    Call<ApiResponse<Map<String, Object>>> getAdminDashboard(@Header("Authorization") String token);
    
    @GET("api/admin/reportes")
    Call<ApiResponse<Map<String, Object>>> getAdminReports(@Header("Authorization") String token, @Query("fecha_inicio") String fechaInicio, @Query("fecha_fin") String fechaFin);
    
    @GET("api/admin/usuarios")
    Call<ApiResponse<List<User>>> getUsers(@Header("Authorization") String token, @Query("tipo") String tipo);
    
    @GET("api/admin/usuarios/{id}")
    Call<ApiResponse<Map<String, Object>>> getUserDetails(@Header("Authorization") String token, @Path("id") int userId);
    
    @POST("api/admin/usuarios/{id}/toggle")
    Call<ApiResponse<Map<String, Object>>> toggleUser(@Header("Authorization") String token, @Path("id") int userId);
    
    @GET("api/admin/cotizaciones/pendientes")
    Call<ApiResponse<List<Quotation>>> getPendingQuotations(@Header("Authorization") String token);
    
    @GET("api/admin/cotizaciones")
    Call<ApiResponse<List<Quotation>>> getAdminQuotations(@Header("Authorization") String token, @Query("estado") String estado, @Query("limit") Integer limit);
    
    @POST("api/admin/cotizaciones/{id}/responder")
    Call<ApiResponse<Void>> respondToQuotation(@Header("Authorization") String token, @Path("id") int quotationId, @Body Map<String, Object> responseData);
    
    @POST("api/admin/cotizaciones/{id}/completar")
    Call<ApiResponse<Void>> completeService(@Header("Authorization") String token, @Path("id") int quotationId, @Body Map<String, Object> completionData);
    
    @POST("api/admin/cotizaciones/{id}/cancelar")
    Call<ApiResponse<Void>> cancelQuotation(@Header("Authorization") String token, @Path("id") int quotationId);
    
    @GET("api/admin/historial")
    Call<ApiResponse<List<Map<String, Object>>>> getAdminHistory(@Header("Authorization") String token, @Query("fecha_inicio") String fechaInicio, @Query("fecha_fin") String fechaFin, @Query("servicio_id") Integer servicioId, @Query("usuario_id") Integer usuarioId, @Query("limit") Integer limit);
    
    @GET("api/admin/clientes-frecuentes")
    Call<ApiResponse<List<Map<String, Object>>>> getFrequentClients(@Header("Authorization") String token, @Query("limit") Integer limit);
    
    @POST("api/admin/promocion")
    Call<ApiResponse<Map<String, Object>>> sendPromotion(@Header("Authorization") String token, @Body Map<String, Object> promotionData);
    
    @POST("api/admin/recordatorios")
    Call<ApiResponse<Map<String, Object>>> scheduleReminders(@Header("Authorization") String token);
    
    @POST("api/admin/notificaciones/limpiar")
    Call<ApiResponse<Map<String, Object>>> cleanNotifications(@Header("Authorization") String token, @Query("dias") Integer dias);
}
