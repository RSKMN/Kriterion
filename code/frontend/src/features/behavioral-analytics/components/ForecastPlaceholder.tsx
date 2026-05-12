import React from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { TrendingUp } from 'lucide-react';
import { PredictionResponse } from '@/types/behavioral';

interface ForecastPlaceholderProps {
    prediction?: PredictionResponse | null;
}

export const ForecastPlaceholder: React.FC<ForecastPlaceholderProps> = ({ prediction }) => {
    const risk = prediction?.overspendingRisk || 'unknown';
    const trend = prediction?.trendDirection || 'unknown';
    const confidence = prediction?.confidenceScore ?? 0;
    const isFallback = prediction?.isFallback ?? true;

    return (
        <Card className="border-none shadow-sm bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm h-full">
            <CardHeader className="pb-2">
                <CardTitle className="text-lg font-semibold flex items-center gap-2">
                    <TrendingUp className="w-5 h-5 text-emerald-500" />
                    Predictive Forecast
                </CardTitle>
                <CardDescription>
                    Forecast confidence improves as recurring transaction structure emerges.
                </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
                <div className="flex flex-wrap items-center gap-2">
                    <Badge variant={risk === 'high' ? 'destructive' : 'secondary'}>{risk} overspending risk</Badge>
                    <Badge variant="outline">trend: {trend}</Badge>
                    <Badge variant="outline">{Math.round(confidence * 100)}% confidence</Badge>
                </div>
                <p className="text-sm text-zinc-600 dark:text-zinc-400 leading-relaxed">
                    {prediction?.forecastNarrative || 'Predictive forecasts become more accurate with recurring transaction patterns.'}
                </p>
                {prediction?.supportingSignals?.length ? (
                    <div className="flex flex-wrap gap-2">
                        {prediction.supportingSignals.map((signal) => (
                            <Badge key={signal} variant="outline" className="text-[10px] uppercase tracking-wider text-zinc-500">
                                {signal}
                            </Badge>
                        ))}
                    </div>
                ) : (
                    <p className="text-xs text-zinc-500">Insufficient data for supporting predictive signals.</p>
                )}
                {isFallback && (
                    <p className="text-xs text-amber-600 dark:text-amber-400">
                        Low-confidence mode active until more dense behavior patterns are detected.
                    </p>
                )}
            </CardContent>
        </Card>
    );
};