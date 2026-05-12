import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { BehavioralMetrics } from '@/types/behavioral';
import { Zap, TrendingUp, BarChart3 } from 'lucide-react';

interface VolatilityVisualizationProps {
    metrics: BehavioralMetrics;
}

export const VolatilityVisualization: React.FC<VolatilityVisualizationProps> = ({ metrics }) => {
    const getLevel = (val: number) => {
        if (!val) return { label: 'Stable', color: 'text-emerald-500 bg-emerald-50 dark:bg-emerald-950/20' };
        if (val > 1.2) return { label: 'High', color: 'text-red-500 bg-red-50 dark:bg-red-950/20' };
        if (val > 0.6) return { label: 'Moderate', color: 'text-amber-500 bg-amber-50 dark:bg-amber-950/20' };
        return { label: 'Stable', color: 'text-emerald-500 bg-emerald-50 dark:bg-emerald-950/20' };
    };

    const volatility = getLevel(metrics.spendingVolatility);

    return (
        <Card className="border-none shadow-sm bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm h-full">
            <CardHeader className="pb-2">
                <CardTitle className="text-lg font-semibold flex items-center gap-2">
                    <Zap className="w-5 h-5 text-amber-500" />
                    Spending Volatility
                </CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
                <div className="flex items-end justify-between">
                    <div className="space-y-1">
                        <span className="text-xs font-medium text-zinc-400 uppercase tracking-widest">Current Signal</span>
                        <div className="text-3xl font-bold text-zinc-900 dark:text-zinc-100 flex items-center gap-2">
                            {metrics.spendingVolatility?.toFixed(2) || '0.00'}
                            <Badge className={volatility.color}>{volatility.label}</Badge>
                        </div>
                    </div>
                    <div className="text-right">
                        <span className="text-xs font-medium text-zinc-400 uppercase tracking-widest">Burst Frequency</span>
                        <div className="text-lg font-semibold">{((metrics.spendingBurstFrequency || 0) * 100).toFixed(0)}%</div>
                    </div>
                </div>

                <div className="space-y-4 pt-2">
                    <div className="flex items-center gap-4">
                        <div className="p-2 rounded-lg bg-indigo-50 dark:bg-indigo-950/30">
                            <TrendingUp className="w-4 h-4 text-indigo-500" />
                        </div>
                        <div className="flex-1 space-y-1">
                            <div className="flex justify-between text-sm">
                                <span className="text-muted-foreground font-medium">Discretionary Ratio</span>
                                <span className="font-mono">{((metrics.discretionarySpendingRatio || 0) * 100).toFixed(0)}%</span>
                            </div>
                            <div className="h-1.5 w-full bg-zinc-100 dark:bg-zinc-800 rounded-full overflow-hidden">
                                <div 
                                    className="h-full bg-indigo-500 transition-all duration-500" 
                                    style={{ width: `${(metrics.discretionarySpendingRatio || 0) * 100}%` }}
                                />
                            </div>
                        </div>
                    </div>

                    <div className="flex items-center gap-4">
                        <div className="p-2 rounded-lg bg-rose-50 dark:bg-rose-950/30">
                            <BarChart3 className="w-4 h-4 text-rose-500" />
                        </div>
                        <div className="flex-1 space-y-1">
                            <div className="flex justify-between text-sm">
                                <span className="text-muted-foreground font-medium">Category Instability</span>
                                <span className="font-mono">{(metrics.categoryInstability || 0).toFixed(2)}</span>
                            </div>
                            <div className="h-1.5 w-full bg-zinc-100 dark:bg-zinc-800 rounded-full overflow-hidden">
                                <div 
                                    className="h-full bg-rose-500 transition-all duration-500" 
                                    style={{ width: `${((metrics.categoryInstability || 0) / 4) * 100}%` }}
                                />
                            </div>
                        </div>
                    </div>
                </div>

                <p className="text-[11px] text-zinc-400 italic leading-relaxed">
                    * Volatility estimates are derived from coefficient of variation in transaction amounts within discretionary categories.
                </p>
            </CardContent>
        </Card>
    );
};
