import React, { useEffect, useState } from 'react';
import { behavioralService } from '@/services/api/behavioral.service';
import {
    BehavioralMetrics,
    BehavioralPattern,
    CognitiveLoadResponse,
    FinancialReflection,
    NarrativeResponse,
    PredictionResponse,
} from '@/types/behavioral';
import { CognitiveLoadCard } from './CognitiveLoadCard';
import { FinancialRhythmCard } from './FinancialRhythmCard';
import { VolatilityVisualization } from './VolatilityVisualization';
import { ReflectionCards } from './ReflectionCards';
import { BehavioralEmptyState } from './BehavioralEmptyState';
import { CognitiveLoadPlaceholder } from './CognitiveLoadPlaceholder';
import { ForecastPlaceholder } from './ForecastPlaceholder';
import { InsightPlaceholderCards } from './InsightPlaceholderCards';
import { LowConfidenceState } from './LowConfidenceState';
import { Skeleton } from '@/components/ui/skeleton';
import { Brain, LineChart, MessageSquare, History, RefreshCw } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

const defaultMetrics: BehavioralMetrics = {
    transactionFrequency: 0,
    decisionDensity: 0,
    spendingVolatility: 0,
    categoryInstability: 0,
    discretionarySpendingRatio: 0,
    recurringObligationPressure: 0,
    lateNightSpendingRatio: 0,
    spendingBurstFrequency: 0,
    averageTransactionFragmentation: 0,
    transactionRhythmConsistency: 0,
    lastComputedAt: new Date().toISOString(),
    hourlyPatterns: {},
    dailyPatterns: {},
    weeklyPatterns: {},
    monthlyPatterns: {},
    postSalarySpendingShift: 0,
};

const defaultCognitiveLoad: CognitiveLoadResponse = {
    cognitiveLoadScore: 0,
    volatilityScore: 0,
    rhythmStabilityScore: 0,
    spendingStructureScore: 0,
    obligationPressureScore: 0,
    factors: [],
    interpretations: ['Not enough behavioral history yet to estimate cognitive load.'],
    status: 'INSUFFICIENT_DATA',
};

const defaultNarrative: NarrativeResponse = {
    narrative: 'Not enough behavioral history yet to synthesize a narrative. As more transactions are recorded, Kriterion will identify rhythms, volatility shifts, and recurring obligations.',
    summary: 'Insufficient Data',
    confidence: 'LOW',
    isFallback: true,
};

const defaultPrediction: PredictionResponse = {
    overspendingRisk: 'unknown',
    trendDirection: 'unknown',
    confidenceScore: 0,
    confidenceState: 'insufficient_data',
    forecastNarrative: 'Predictive forecasts become more accurate with recurring transaction patterns.',
    supportingSignals: [],
    status: 'INSUFFICIENT_DATA',
    isFallback: true,
};

export const BehavioralDashboard: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const [metrics, setMetrics] = useState<BehavioralMetrics>(defaultMetrics);
    const [patterns, setPatterns] = useState<BehavioralPattern[]>([]);
    const [cognitiveLoad, setCognitiveLoad] = useState<CognitiveLoadResponse>(defaultCognitiveLoad);
    const [insights, setInsights] = useState<FinancialReflection[]>([]);
    const [narrative, setNarrative] = useState<NarrativeResponse>(defaultNarrative);
    const [prediction, setPrediction] = useState<PredictionResponse>(defaultPrediction);
    const [isSeeding, setIsSeeding] = useState(false);

    const fetchData = async () => {
        setLoading(true);

        const results = await Promise.allSettled([
            behavioralService.getMetrics(),
            behavioralService.getPatterns(),
            behavioralService.getCognitiveLoad(),
            behavioralService.getInsights(),
            behavioralService.getNarrative(),
            behavioralService.getPrediction(),
        ]);

        const [metricsResult, patternsResult, cognitiveResult, insightsResult, narrativeResult, predictionResult] = results;

        setMetrics(metricsResult.status === 'fulfilled' && metricsResult.value ? metricsResult.value : defaultMetrics);
        setPatterns(patternsResult.status === 'fulfilled' && patternsResult.value ? patternsResult.value : []);
        setCognitiveLoad(cognitiveResult.status === 'fulfilled' && cognitiveResult.value ? cognitiveResult.value : defaultCognitiveLoad);
        setInsights(insightsResult.status === 'fulfilled' && insightsResult.value ? insightsResult.value : []);
        setNarrative(narrativeResult.status === 'fulfilled' && narrativeResult.value ? narrativeResult.value : defaultNarrative);
        setPrediction(predictionResult.status === 'fulfilled' && predictionResult.value ? predictionResult.value : defaultPrediction);

        if (results.some((result) => result.status === 'rejected')) {
            console.error('One or more behavioral requests failed; defaults applied where needed.');
        }

        setLoading(false);
        setIsSeeding(false);
    };

    const handleSeed = async () => {
        setIsSeeding(true);
        try {
            await behavioralService.seedData();
            window.setTimeout(() => {
                void fetchData();
            }, 2000);
        } catch (error) {
            console.error('Seeding failed:', error);
            setIsSeeding(false);
        }
    };

    useEffect(() => {
        void fetchData();
    }, []);

    if (loading) {
        return (
            <div className="space-y-6 animate-pulse">
                <div className="flex justify-between items-center">
                    <Skeleton className="h-10 w-64" />
                    <Skeleton className="h-10 w-24" />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <Skeleton className="h-64 rounded-xl" />
                    <Skeleton className="h-64 rounded-xl" />
                    <Skeleton className="h-64 rounded-xl" />
                </div>
                <Skeleton className="h-96 rounded-xl" />
            </div>
        );
    }

    const isDataInsufficient =
        metrics.transactionFrequency === 0 &&
        cognitiveLoad.status === 'INSUFFICIENT_DATA' &&
        insights.length === 0 &&
        patterns.length === 0;
    const isLowConfidenceForecast = prediction.isFallback || prediction.confidenceScore < 0.35;

    return (
        <div className="space-y-8 pb-12">
            <header className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight text-zinc-900 dark:text-zinc-50 flex items-center gap-3">
                        <Brain className="w-8 h-8 text-indigo-500" />
                        Behavioral Intelligence
                    </h1>
                    <p className="text-zinc-500 dark:text-zinc-400 mt-1">
                        Quantitative analysis of financial decision patterns and cognitive complexity.
                    </p>
                </div>
                <div className="flex items-center gap-2">
                    <Button variant="outline" size="sm" className="gap-2" onClick={fetchData} disabled={loading}>
                        <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
                        Re-analyze
                    </Button>
                </div>
            </header>

            {isDataInsufficient ? (
                <BehavioralEmptyState onSeed={handleSeed} isSeeding={isSeeding} />
            ) : (
                <Tabs defaultValue="overview" className="space-y-6">
                    <TabsList className="bg-zinc-100 dark:bg-zinc-800/50 p-1 rounded-xl">
                        <TabsTrigger value="overview" className="rounded-lg gap-2 data-[state=active]:bg-white dark:data-[state=active]:bg-zinc-800">
                            <LineChart className="w-4 h-4" /> Overview
                        </TabsTrigger>
                        <TabsTrigger value="insights" className="rounded-lg gap-2 data-[state=active]:bg-white dark:data-[state=active]:bg-zinc-800">
                            <MessageSquare className="w-4 h-4" /> Insights
                        </TabsTrigger>
                        <TabsTrigger value="patterns" className="rounded-lg gap-2 data-[state=active]:bg-white dark:data-[state=active]:bg-zinc-800">
                            <History className="w-4 h-4" /> Observed Patterns
                        </TabsTrigger>
                    </TabsList>

                    <TabsContent value="overview" className="space-y-6">
                        {isLowConfidenceForecast && (
                            <LowConfidenceState
                                title="Forecast confidence is limited"
                                message={prediction.forecastNarrative || 'Predictive forecasts require more recurring transaction patterns before they become stable.'}
                            />
                        )}
                        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                            {cognitiveLoad.status === 'INSUFFICIENT_DATA' ? <CognitiveLoadPlaceholder /> : <CognitiveLoadCard data={cognitiveLoad} />}
                            {metrics.transactionFrequency > 0 ? <FinancialRhythmCard metrics={metrics} /> : <CognitiveLoadPlaceholder />}
                            {metrics.transactionFrequency > 0 ? <VolatilityVisualization metrics={metrics} /> : <CognitiveLoadPlaceholder />}
                        </div>
                        <ForecastPlaceholder prediction={prediction} />
                    </TabsContent>

                    <TabsContent value="insights" className="space-y-6">
                        {insights.length > 0 ? (
                            <ReflectionCards insights={insights} narrative={narrative} />
                        ) : (
                            <InsightPlaceholderCards />
                        )}
                    </TabsContent>

                    <TabsContent value="patterns" className="space-y-6">
                        {patterns.length > 0 ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {patterns.map((pattern) => (
                                    <Card key={pattern.id} className="border-none shadow-sm bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm">
                                        <CardHeader className="pb-2">
                                            <div className="flex justify-between items-center">
                                                <CardTitle className="text-sm font-semibold uppercase tracking-wider text-zinc-500">
                                                    {pattern.patternName?.replace(/_/g, ' ') || 'UNKNOWN PATTERN'}
                                                </CardTitle>
                                                <div className={`h-2 w-2 rounded-full ${pattern.isActive ? 'bg-emerald-500 animate-pulse' : 'bg-zinc-300'}`} />
                                            </div>
                                        </CardHeader>
                                        <CardContent>
                                            <div className="space-y-3">
                                                <div className="flex justify-between text-sm">
                                                    <span className="text-muted-foreground">Confidence</span>
                                                    <span className="font-mono">{((pattern.confidenceScore || 0) * 100).toFixed(0)}%</span>
                                                </div>
                                                <div className="text-[11px] text-zinc-400">
                                                    Last observed: {pattern.lastObservedAt ? new Date(pattern.lastObservedAt).toLocaleDateString() : 'N/A'}
                                                </div>
                                            </div>
                                        </CardContent>
                                    </Card>
                                ))}
                            </div>
                        ) : (
                            <Card className="border-dashed border-zinc-200 bg-white/50 dark:bg-zinc-900/40 backdrop-blur-sm">
                                <CardContent className="p-6 text-sm text-zinc-500">
                                    No active behavioral patterns detected yet. Pattern detection becomes meaningful after repeated salary, subscription, and burst spending cycles are present.
                                </CardContent>
                            </Card>
                        )}
                    </TabsContent>
                </Tabs>
            )}
        </div>
    );
};