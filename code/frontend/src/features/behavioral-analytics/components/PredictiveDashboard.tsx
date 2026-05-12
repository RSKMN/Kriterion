import React, { useEffect, useState } from 'react';
import { behavioralService } from '@/services/api/behavioral.service';
import { PredictionResponse } from '@/types/behavioral';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import { Radar, RefreshCw } from 'lucide-react';
import { ForecastPlaceholder } from './ForecastPlaceholder';
import { PredictiveEmptyState } from './PredictiveEmptyState';
import { LowConfidenceState } from './LowConfidenceState';

export const PredictiveDashboard: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const [prediction, setPrediction] = useState<PredictionResponse | null>(null);

    const fetchPrediction = async () => {
        setLoading(true);
        try {
            const result = await behavioralService.getPrediction();
            setPrediction(result);
        } catch (error) {
            console.error('Error fetching predictive data:', error);
            setPrediction(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        void fetchPrediction();
    }, []);

    if (loading) {
        return (
            <div className="space-y-4">
                <div className="flex justify-between items-center">
                    <Skeleton className="h-8 w-56" />
                    <Skeleton className="h-10 w-24" />
                </div>
                <Skeleton className="h-64 rounded-xl" />
            </div>
        );
    }

    const isLowConfidence = !prediction || prediction.isFallback || prediction.confidenceScore < 0.35;

    return (
        <div className="space-y-6 pb-12">
            <header className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h2 className="text-2xl font-bold tracking-tight text-zinc-900 dark:text-zinc-50 flex items-center gap-3">
                        <Radar className="w-7 h-7 text-emerald-500" />
                        Predictive Analytics
                    </h2>
                    <p className="text-zinc-500 dark:text-zinc-400 mt-1">
                        Forecasting based on volatility, recurring obligations, and temporal spending structure.
                    </p>
                </div>
                <Button variant="outline" size="sm" className="gap-2" onClick={fetchPrediction}>
                    <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
                    Refresh
                </Button>
            </header>

            {isLowConfidence ? (
                <div className="space-y-4">
                    <PredictiveEmptyState />
                    {prediction && (
                        <LowConfidenceState
                            title="Forecast confidence is still limited"
                            message={prediction.forecastNarrative || 'Predictive forecasts require more recurring patterns before they are stable enough for stronger interpretation.'}
                        />
                    )}
                </div>
            ) : (
                <Card className="border-none shadow-sm bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm">
                    <CardHeader className="pb-2">
                        <CardTitle className="text-lg font-semibold text-zinc-800 dark:text-zinc-200">Forecast Summary</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <ForecastPlaceholder prediction={prediction} />
                    </CardContent>
                </Card>
            )}
        </div>
    );
};