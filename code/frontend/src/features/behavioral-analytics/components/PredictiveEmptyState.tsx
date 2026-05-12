import React from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Radar } from 'lucide-react';

export const PredictiveEmptyState: React.FC = () => {
    return (
        <Card className="border-dashed border-zinc-200 bg-white/50 dark:bg-zinc-900/40 backdrop-blur-sm">
            <CardHeader>
                <CardTitle className="flex items-center gap-2 text-zinc-800 dark:text-zinc-200">
                    <Radar className="w-5 h-5 text-emerald-500" />
                    Predictive Analytics
                </CardTitle>
                <CardDescription>
                    Predictive forecasts become more accurate with recurring transaction patterns.
                </CardDescription>
            </CardHeader>
            <CardContent>
                <p className="text-sm text-zinc-500 leading-relaxed">
                    The current dataset is too sparse for stable forecasting. As salary cycles, subscriptions, and discretionary bursts accumulate, predictive signals will become more actionable.
                </p>
            </CardContent>
        </Card>
    );
};