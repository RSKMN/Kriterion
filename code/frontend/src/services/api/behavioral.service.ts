import { 
    BehavioralMetrics, 
    BehavioralPattern, 
    CognitiveLoadResponse, 
    FinancialReflection, 
    NarrativeResponse,
    PredictionResponse 
} from '../../types/behavioral';
import apiClient from './client';

export const behavioralService = {
    getMetrics: async (): Promise<BehavioralMetrics> => {
        const response = await apiClient.get('/analytics/behavioral');
        return response.data.data;
    },

    getPatterns: async (): Promise<BehavioralPattern[]> => {
        const response = await apiClient.get('/analytics/patterns');
        return response.data.data;
    },

    getCognitiveLoad: async (): Promise<CognitiveLoadResponse> => {
        const response = await apiClient.get('/analytics/cognitive-load');
        return response.data.data;
    },

    getInsights: async (): Promise<FinancialReflection[]> => {
        const response = await apiClient.get('/analytics/insights');
        return response.data.data;
    },

    getNarrative: async (): Promise<NarrativeResponse> => {
        const response = await apiClient.get('/ai/narrative');
        return response.data.data;
    },

    getPrediction: async (): Promise<PredictionResponse> => {
        const response = await apiClient.get('/analytics/predictions');
        return response.data.data;
    },

    seedData: async (): Promise<void> => {
        await apiClient.post('/debug/seed/behavioral');
    }
};
